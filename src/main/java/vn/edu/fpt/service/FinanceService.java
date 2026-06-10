package vn.edu.fpt.service;

import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Settlement;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.request.finance.EventSettlementDTO;
import vn.edu.fpt.modelview.request.finance.FinanceDashBoardDTO;

import java.util.List;
import java.util.Set;

public interface FinanceService {

    FinanceDashBoardDTO getDashboardStats();
    List<Settlement> getRecentSettlements();
    List<Event> getAllEvents();
    List<Event> getEndedEvents();
    List<Event> getEventsByStatus(EventStatus status);
    Set<Long> getSettledEventIds(List<Event> events);
    long countAwaitingSettlement(List<Event> events);
    double getTotalRevenue();
    List<EventSettlementDTO> getUnsettledEvents();
    void createSettlement(Long eventId, Double refundDeduction, String paymentMethod, String notes);




}