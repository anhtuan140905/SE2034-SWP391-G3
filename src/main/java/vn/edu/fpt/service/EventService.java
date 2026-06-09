package vn.edu.fpt.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.EventCategory;
import vn.edu.fpt.model.Venue;
import vn.edu.fpt.model.VenueZone;
import vn.edu.fpt.modelview.request.admin.VenueZoneDTO;
import vn.edu.fpt.modelview.request.moderator.DashboardStatsDTO;
import vn.edu.fpt.modelview.request.organizer.AddressDto;
import vn.edu.fpt.modelview.request.organizer.EventDTO;
import vn.edu.fpt.modelview.request.organizer.VenueDto;
import vn.edu.fpt.modelview.request.organizer.VenueZoneOrganizerDTO;
import vn.edu.fpt.modelview.request.moderator.EventDetailModeratorDTO;
import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;
import vn.edu.fpt.repository.FeaturedEventDTO;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    long countHostedEvents();
    List<EventSummaryDto> findTopFeaturedEvents();
    FeaturedEventDTO findFeaturedEvent();
    List<VenueDto> findByDateNot(LocalDate dates);
    List<EventCategory> getListEventCategory();
    List<VenueZoneOrganizerDTO> getVenueZoneByVenueId(Long id);
    void saveEvent(EventDTO eventDTO);
    VenueDto getVenuebyId(Long venueID);
    EventDetailModeratorDTO getEventDetailById(Long id);
    DashboardStatsDTO getDashboardStats();
    List<Event> getTopThreePendingEvents();
    List<Event> getTodayActiveEvents();
}