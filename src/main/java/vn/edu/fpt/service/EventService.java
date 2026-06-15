package vn.edu.fpt.service;

import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;

import java.util.List;


public interface EventService {
//    long countHostedEvents();
//    List<EventSummaryDto> findTopFeaturedEvents();
//    FeaturedEventDTO findFeaturedEvent();
//    List<EventCategory> getListEventCategory();
//    void saveEvent(EventDTO eventDTO);
//    EventDetailModeratorDTO getEventDetailById(Long id);
//    DashboardStatsDTO getDashboardStats();
//    List<Event> getTopThreePendingEvents();
//    List<Event> getTodayActiveEvents();
//    Page<Event> searchEvents(EventSearchCriteria criteria, Pageable pageable);
//    EventSummaryProjection findEventDetailById(Long id);
//    Page<EventCardDTO> getEventCards(Long organizerId, String[] statuses, String keyword, int page);
List<EventSummaryDto> findTop10Events();
}
