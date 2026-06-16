package vn.edu.fpt.service;


import vn.edu.fpt.model.EventCategory;
import vn.edu.fpt.modelview.request.organizer.EventDTO;
import vn.edu.fpt.modelview.request.organizer.cityDto;
import vn.edu.fpt.modelview.request.organizer.wardDTO;

import java.util.List;

public interface EventService {
//    long countHostedEvents();
//    List<EventSummaryDto> findTopFeaturedEvents();
//    FeaturedEventDTO findFeaturedEvent();
    List<cityDto> getListcity();
    List<EventCategory> getListEventCategory();
    List<wardDTO> listWardDtos(Long cityId);
    void saveEvent(EventDTO eventDTO);
//    EventDetailModeratorDTO getEventDetailById(Long id);
//    DashboardStatsDTO getDashboardStats();
//    List<Event> getTopThreePendingEvents();
//    List<Event> getTodayActiveEvents();
//    Page<Event> searchEvents(EventSearchCriteria criteria, Pageable pageable);
//    EventSummaryProjection findEventDetailById(Long id);
//    Page<EventCardDTO> getEventCards(Long organizerId, String[] statuses, String keyword, int page);
}
