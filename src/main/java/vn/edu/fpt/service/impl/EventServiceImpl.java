package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Venue;
import vn.edu.fpt.modelview.request.moderator.EventDetailModeratorDTO;
import vn.edu.fpt.repository.CreateEventRepository;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.service.EventService;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service("EventService")
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private CreateEventRepository createEventRepository;

    private final EventRepository eventRepository;

    @Override
    public Event createEvent(Event event) {
        return null;
    }

    @Override
    public List<Venue> findByDateNot(LocalDate dates) {
        List<Venue> venues = createEventRepository.findByDateNot(dates);
        return venues;
    }

    @Override
    public EventDetailModeratorDTO getEventDetailById(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->new RuntimeException("Không tìm thấy sự kiện"));

        EventDetailModeratorDTO eventDetailModeratorDTO = new EventDetailModeratorDTO();
        eventDetailModeratorDTO.setId(event.getEventId());
        eventDetailModeratorDTO.setTitle(event.getTitle());
        eventDetailModeratorDTO.setStatus(event.getStatus().name());
        eventDetailModeratorDTO.setCoverImageUrl(event.getThumbnailUrl());

        // lay ten category
        eventDetailModeratorDTO.setCategoryId(event.getCategory().getCategoryId());
        eventDetailModeratorDTO.setCategory(event.getCategory().getCategoryName());
        eventDetailModeratorDTO.setDescription(event.getDescription());
        eventDetailModeratorDTO.setStartTime(event.getStartTime());

        // Tinh toan thoi luong Duration
        if(event.getStartTime() != null && event.getEndTime() != null) {
            long hours = Duration.between(event.getStartTime(), event.getEndTime()).toHours();
            eventDetailModeratorDTO.setDuration(hours + " hours");
        } else {
            eventDetailModeratorDTO.setDuration("N/A");
        }

        // Mapping venue (Venue & Address)
        if (event.getVenue() != null) {
            eventDetailModeratorDTO.setVenueName(event.getVenue().getVenueName());
            eventDetailModeratorDTO.setCapacity(event.getVenue().getCapacity());
            eventDetailModeratorDTO.setTotalTickets(event.getVenue().getCapacity());

            if (event.getVenue().getAddress() != null) {
                eventDetailModeratorDTO.setVenueAddress(event.getVenue().getAddress().getSpecificAddress());
            }
        }

        // Mapping organizer (Organizer / User)
        if (event.getOrganizer() != null) {
            eventDetailModeratorDTO.setOrganizerId(event.getOrganizer().getId());
            String fullName = event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName();
            eventDetailModeratorDTO.setOrganizerName(fullName);
            eventDetailModeratorDTO.setOrganizerAvatarUrl(event.getOrganizer().getAvatar());
        }

        //truong gia dinh (se dung khi database update them cot)
        eventDetailModeratorDTO.setRejectReason("");

        return eventDetailModeratorDTO;
    }
}



