package vn.edu.fpt.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.EventCategory;
import vn.edu.fpt.modelview.request.homepage.EventSearchCriteria;
import vn.edu.fpt.modelview.request.moderator.DashboardStatsDTO;
import vn.edu.fpt.modelview.request.organizer.EventCardDTO;
import vn.edu.fpt.modelview.request.organizer.EventDTO;
import vn.edu.fpt.modelview.request.organizer.VenueDto;
import vn.edu.fpt.modelview.request.organizer.VenueZoneOrganizerDTO;
import vn.edu.fpt.modelview.request.moderator.EventDetailModeratorDTO;
import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;
import vn.edu.fpt.repository.EventSummaryProjection;
import vn.edu.fpt.repository.FeaturedEventDTO;
import vn.edu.fpt.repository.VenueSummaryProjection;

import java.time.LocalDate;
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
}
