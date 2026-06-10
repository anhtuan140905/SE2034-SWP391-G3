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


}