package vn.edu.fpt.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.EventCategory;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.request.organizer.EventCardDTO;
import vn.edu.fpt.modelview.request.organizer.EventDTO;
import vn.edu.fpt.modelview.request.organizer.cityDto;
import vn.edu.fpt.modelview.request.organizer.wardDTO;
import vn.edu.fpt.modelview.request.homepage.EventSearchCriteria;
import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;
import vn.edu.fpt.repository.EventSummaryProjection;
import vn.edu.fpt.repository.FeaturedEventDTO;

import java.util.List;
import vn.edu.fpt.modelview.response.moderator.DashboardStatsDTO;

public interface EventService {
//    long countHostedEvents();
//    List<EventSummaryDto> findTopFeaturedEvents();
//    FeaturedEventDTO findFeaturedEvent();
    List<cityDto> getListcity();
    List<EventCategory> getListEventCategory();
    List<wardDTO> listWardDtos(Long cityId);
    void saveEvent(EventDTO eventDTO);
    long countHostedEvents();
    List<EventSummaryDto> findTopFeaturedEvents();
    FeaturedEventDTO findFeaturedEvent();
//    void saveEvent(EventDTO eventDTO);
//    EventDetailModeratorDTO getEventDetailById(Long id);
//    DashboardStatsDTO getDashboardStats();
//    List<Event> getTopThreePendingEvents();
//    List<Event> getTodayActiveEvents();
    Page<Event> searchEvents(EventSearchCriteria criteria, Pageable pageable);
    Event getEventById(Long id);
    EventSummaryProjection findEventDetailById(Long id);
    Page<EventCardDTO> getEventCards(Long organizerId, String[] statuses, String keyword, int page);
List<EventSummaryDto> findTop10Events();
}
