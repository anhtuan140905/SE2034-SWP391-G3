package vn.edu.fpt.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Settlement;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.model.constant.SettlementStatus;
import vn.edu.fpt.modelview.request.finance.EventSettlementDTO;
import vn.edu.fpt.modelview.request.finance.FinanceDashBoardDTO;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.repository.OrderRepository;
import vn.edu.fpt.repository.SettlementRepository;
import vn.edu.fpt.service.FinanceService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FinanceServiceImpl implements FinanceService {

    private final EventRepository      eventRepository;
    private final SettlementRepository settlementRepository;
    private final OrderRepository      orderRepository;
    private final JavaMailSender       mailSender;

    public FinanceServiceImpl(EventRepository eventRepository,
                              SettlementRepository settlementRepository,
                              OrderRepository orderRepository,
                              JavaMailSender mailSender) {
        this.eventRepository      = eventRepository;
        this.settlementRepository = settlementRepository;
        this.orderRepository      = orderRepository;
        this.mailSender           = mailSender;
    }

    @Override
    public FinanceDashBoardDTO getDashboardStats() {
        BigDecimal totalRevenue = settlementRepository.getTotalRevenue();
        BigDecimal paidAmount   = settlementRepository.getPaidAmount();

        FinanceDashBoardDTO stats = new FinanceDashBoardDTO();
        stats.setTotalRevenue(totalRevenue != null ? totalRevenue.doubleValue() : 0.0);
        stats.setPendingCount(settlementRepository.countByStatus(SettlementStatus.PENDING));
        stats.setPaidAmount(paidAmount != null ? paidAmount.doubleValue() : 0.0);
        stats.setCompletedEvents(eventRepository.countByStatus(EventStatus.ENDED));
        return stats;
    }

    @Override
    public List<Settlement> getRecentSettlements() {
        return settlementRepository.findTop5ByOrderByCreatedAtDesc();
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    @Override
    public List<Event> getEndedEvents() {
        return eventRepository.findByStatus(EventStatus.ENDED);
    }
    @Override
    public Set<Long> getSettledEventIds(List<Event> events) {
        return events.stream()
                .filter(e -> settlementRepository.existsByEvent(e))
                .map(Event::getEventId)
                .collect(Collectors.toSet());
    }
    @Override
    public long countAwaitingSettlement(List<Event> events) {
        return events.stream()
                .filter(e -> !settlementRepository.existsByEvent(e))
                .count();
    }
    @Override
    public double getTotalRevenue() {
        BigDecimal total = settlementRepository.getTotalRevenue();
        return total != null ? total.doubleValue() : 0.0;
    }
    @Override
    public List<Event> getEventsByStatus(EventStatus status) {
        return eventRepository.findByStatus(status);
    }
    @Override
    public List<EventSettlementDTO> getUnsettledEvents() {
        return eventRepository.findByStatus(EventStatus.ENDED)
                .stream()
                .filter(e -> !settlementRepository.existsByEvent(e))
                .map(e -> new EventSettlementDTO(
                        e.getEventId(),
                        e.getTitle(),
                        e.getOrganizer().getFirstName() + " " + e.getOrganizer().getLastName(),
                        calculateRevenue(e)
                ))
                .toList();
    }

    @Override
    @Transactional
    public void createSettlement(Long eventId, Double refundDeduction, String paymentMethod, String notes) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        if (settlementRepository.existsByEvent(event)) {
            throw new IllegalStateException("Settlement already exists for this event.");
        }

        BigDecimal grossRevenue = orderRepository.sumTotalAmountByEventAndStatus(event, OrderStatus.PAID);
        if (grossRevenue == null) grossRevenue = BigDecimal.ZERO;
        grossRevenue = grossRevenue.subtract(BigDecimal.valueOf(refundDeduction));

        BigDecimal platformFee  = grossRevenue.multiply(BigDecimal.valueOf(0.10));
        BigDecimal payoutAmount = grossRevenue.subtract(platformFee);

        Settlement settlement = new Settlement();
        settlement.setEvent(event);
        settlement.setGrossRevenue(grossRevenue);
        settlement.setPlatformFee(platformFee);
        settlement.setPayoutAmount(payoutAmount);
        settlement.setStatus(SettlementStatus.PENDING);
        settlementRepository.save(settlement);
    }
    private BigDecimal calculateRevenue(Event event) {
        BigDecimal revenue = orderRepository.sumTotalAmountByEventAndStatus(event, OrderStatus.PAID);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
    @Override
    public List<Settlement> getAllSettlements() {
        return settlementRepository.findAll();
    }

    @Override
    public List<Settlement> getSettlementsByStatus(String status) {
        if (status == null || status.equalsIgnoreCase("ALL")) {
            return settlementRepository.findAll();
        }
        try {
            return settlementRepository.findByStatus(SettlementStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return settlementRepository.findAll();
        }
    }





}