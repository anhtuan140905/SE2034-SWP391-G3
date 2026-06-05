package vn.edu.fpt.service;

import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.EventCategory;
import vn.edu.fpt.model.Venue;
import vn.edu.fpt.model.VenueZone;
import vn.edu.fpt.modelview.request.admin.VenueZoneDTO;
import vn.edu.fpt.modelview.request.organizer.AddressDto;
import vn.edu.fpt.modelview.request.organizer.EventDTO;
import vn.edu.fpt.modelview.request.organizer.VenueDto;
import vn.edu.fpt.modelview.request.organizer.VenueZoneOrganizerDTO;
import vn.edu.fpt.modelview.request.moderator.EventDetailModeratorDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<VenueDto> findByDateNot(LocalDate dates);
    List<EventCategory> getListEventCategory();
    List<VenueZoneOrganizerDTO> getVenueZoneByVenueId(Long id);
    void saveEvent(EventDTO eventDTO);
    VenueDto getVenuebyId(Long venueID);
    EventDetailModeratorDTO getEventDetailById(Long id);
}