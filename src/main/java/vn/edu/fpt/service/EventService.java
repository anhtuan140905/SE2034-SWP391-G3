package vn.edu.fpt.service;

import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;

import java.util.List;
import vn.edu.fpt.modelview.response.moderator.DashboardStatsDTO;


public interface EventService {
//    long countHostedEvents();
//    List<EventSummaryDto> findTopFeaturedEvents();
//    FeaturedEventDTO findFeaturedEvent();
//    List<EventCategory> getListEventCategory();
//    void saveEvent(EventDTO eventDTO);
//    EventDetailModeratorDTO getEventDetailById(Long id);
//    Page<Event> searchEvents(EventSearchCriteria criteria, Pageable pageable);
//    EventSummaryProjection findEventDetailById(Long id);
//    Page<EventCardDTO> getEventCards(Long organizerId, String[] statuses, String keyword, int page);
List<EventSummaryDto> findTop10Events();
}
