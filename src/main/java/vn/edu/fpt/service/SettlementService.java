package vn.edu.fpt.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.Settlement;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.model.constant.SettlementStatus;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.repository.OrderRepository;
import vn.edu.fpt.repository.SettlementRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;

    public SettlementService(SettlementRepository settlementRepository,
                             EventRepository eventRepository,
                             OrderRepository orderRepository) {
        this.settlementRepository = settlementRepository;
        this.eventRepository = eventRepository;
        this.orderRepository = orderRepository;
    }

    public List<Settlement> getAllSettlements() {
        return settlementRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Settlement> getSettlementById(Long id) {
        return settlementRepository.findById(id);
    }

    public Optional<Settlement> getSettlementByEvent(Event event) {
        return settlementRepository.findByEvent(event);
    }

    // Class to wrap ended event data for the view
    public static class EndedEventView {
        private Event event;
        private int ticketsSold;
        private BigDecimal grossRevenue;
        private BigDecimal platformFee;
        private boolean settled;
        private Long settlementId;

        public EndedEventView(Event event, int ticketsSold, BigDecimal grossRevenue, boolean settled, Long settlementId) {
            this.event = event;
            this.ticketsSold = ticketsSold;
            this.grossRevenue = grossRevenue;
            this.platformFee = grossRevenue.multiply(new BigDecimal("0.10"));
            this.settled = settled;
            this.settlementId = settlementId;
        }

        public Event getEvent() { return event; }
        public int getTicketsSold() { return ticketsSold; }
        public BigDecimal getGrossRevenue() { return grossRevenue; }
        public BigDecimal getPlatformFee() { return platformFee; }
        public boolean isSettled() { return settled; }
        public Long getSettlementId() { return settlementId; }
    }

    public List<EndedEventView> getEndedEvents() {
        List<Event> endedEvents = eventRepository.findByStatus(EventStatus.ENDED);
        List<EndedEventView> views = new ArrayList<>();

        for (Event event : endedEvents) {
            List<Order> paidOrders = orderRepository.findByEventAndStatus(event, OrderStatus.PAID);
            
            int ticketsSold = 0;
            BigDecimal grossRevenue = BigDecimal.ZERO;
            
            for (Order order : paidOrders) {
                if (order.getOrderDetails() != null) {
                    ticketsSold += order.getOrderDetails().size();
                }
                if (order.getTotalAmount() != null) {
                    grossRevenue = grossRevenue.add(order.getTotalAmount());
                }
            }

            Optional<Settlement> settlementOpt = settlementRepository.findByEvent(event);
            boolean settled = settlementOpt.isPresent();
            Long settlementId = settled ? settlementOpt.get().getSettlementId() : null;

            views.add(new EndedEventView(event, ticketsSold, grossRevenue, settled, settlementId));
        }

        return views;
    }

    @Transactional
    public Settlement createSettlement(Long eventId, BigDecimal refundDeduction, String paymentMethod, String notes) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

        if (settlementRepository.existsByEvent(event)) {
            throw new IllegalStateException("Settlement already exists for event ID: " + eventId);
        }

        List<Order> paidOrders = orderRepository.findByEventAndStatus(event, OrderStatus.PAID);
        BigDecimal grossRevenue = BigDecimal.ZERO;
        for (Order order : paidOrders) {
            if (order.getTotalAmount() != null) {
                grossRevenue = grossRevenue.add(order.getTotalAmount());
            }
        }

        // Platform fee is 10%
        BigDecimal platformFee = grossRevenue.multiply(new BigDecimal("0.10"));
        BigDecimal payoutAmount = grossRevenue.subtract(platformFee).subtract(refundDeduction != null ? refundDeduction : BigDecimal.ZERO);
        if (payoutAmount.compareTo(BigDecimal.ZERO) < 0) {
            payoutAmount = BigDecimal.ZERO;
        }

        Settlement settlement = new Settlement();
        settlement.setEvent(event);
        settlement.setGrossRevenue(grossRevenue);
        settlement.setPlatformFee(platformFee);
        settlement.setPayoutAmount(payoutAmount);
        settlement.setStatus(SettlementStatus.PENDING);
        settlement.setCreatedBy("Sarah Anderson"); // Default active Finance Officer name
        if (paymentMethod != null && !paymentMethod.isBlank()) {
            settlement.setPaymentMethod(paymentMethod);
        }

        return settlementRepository.save(settlement);
    }

    @Transactional
    public Settlement paySettlement(Long id) {
        Settlement settlement = settlementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Settlement not found with ID: " + id));

        if (settlement.getStatus() == SettlementStatus.PENDING) {
            settlement.setStatus(SettlementStatus.COMPLETED);
            settlement.setPaidAt(Instant.now());
            settlement.setUpdatedBy("Sarah Anderson");
            return settlementRepository.save(settlement);
        }
        return settlement;
    }

    // Statistics DTO
    public static class FinanceStats {
        private BigDecimal totalPlatformRevenue = BigDecimal.ZERO;
        private BigDecimal platformFeesCollected = BigDecimal.ZERO;
        private BigDecimal pendingSettlementsAmount = BigDecimal.ZERO;
        private BigDecimal successfulPayouts = BigDecimal.ZERO;
        private long pendingSettlementsCount;
        private long eventsReadyCount;
        private long successfulPayoutsCount;

        public FinanceStats(BigDecimal totalPlatformRevenue, BigDecimal platformFeesCollected,
                            BigDecimal pendingSettlementsAmount, BigDecimal successfulPayouts,
                            long pendingSettlementsCount, long eventsReadyCount, long successfulPayoutsCount) {
            this.totalPlatformRevenue = totalPlatformRevenue;
            this.platformFeesCollected = platformFeesCollected;
            this.pendingSettlementsAmount = pendingSettlementsAmount;
            this.successfulPayouts = successfulPayouts;
            this.pendingSettlementsCount = pendingSettlementsCount;
            this.eventsReadyCount = eventsReadyCount;
            this.successfulPayoutsCount = successfulPayoutsCount;
        }

        public BigDecimal getTotalPlatformRevenue() { return totalPlatformRevenue; }
        public BigDecimal getPlatformFeesCollected() { return platformFeesCollected; }
        public BigDecimal getPendingSettlementsAmount() { return pendingSettlementsAmount; }
        public BigDecimal getSuccessfulPayouts() { return successfulPayouts; }
        public long getPendingSettlementsCount() { return pendingSettlementsCount; }
        public long getEventsReadyCount() { return eventsReadyCount; }
        public long getSuccessfulPayoutsCount() { return successfulPayoutsCount; }
    }

    public FinanceStats getStatistics() {
        List<Settlement> allSettlements = settlementRepository.findAll();
        
        BigDecimal totalPlatformRevenue = BigDecimal.ZERO;
        BigDecimal platformFeesCollected = BigDecimal.ZERO;
        BigDecimal pendingSettlementsAmount = BigDecimal.ZERO;
        BigDecimal successfulPayouts = BigDecimal.ZERO;
        
        long pendingSettlementsCount = 0;
        long successfulPayoutsCount = 0;

        for (Settlement s : allSettlements) {
            if (s.getStatus() == SettlementStatus.COMPLETED) {
                totalPlatformRevenue = totalPlatformRevenue.add(s.getGrossRevenue());
                platformFeesCollected = platformFeesCollected.add(s.getPlatformFee());
                successfulPayouts = successfulPayouts.add(s.getPayoutAmount());
                successfulPayoutsCount++;
            } else if (s.getStatus() == SettlementStatus.PENDING) {
                pendingSettlementsAmount = pendingSettlementsAmount.add(s.getPayoutAmount());
                pendingSettlementsCount++;
            }
        }

        // Calculate completed events awaiting settlement
        long eventsReadyCount = 0;
        List<Event> endedEvents = eventRepository.findByStatus(EventStatus.ENDED);
        for (Event event : endedEvents) {
            if (!settlementRepository.existsByEvent(event)) {
                eventsReadyCount++;
            }
        }

        return new FinanceStats(
                totalPlatformRevenue,
                platformFeesCollected,
                pendingSettlementsAmount,
                successfulPayouts,
                pendingSettlementsCount,
                eventsReadyCount,
                successfulPayoutsCount
        );
    }
}
