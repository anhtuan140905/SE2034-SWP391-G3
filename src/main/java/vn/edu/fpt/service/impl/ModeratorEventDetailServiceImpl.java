package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.response.moderator.ModeratorEventDetailDTO;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.service.ModeratorEventDetailService;

@Service
@RequiredArgsConstructor
public class ModeratorEventDetailServiceImpl implements ModeratorEventDetailService {

    private final EventRepository eventRepository;
    private final EmailService emailService;

    @Override
    @Transactional (readOnly = true)
    public ModeratorEventDetailDTO getEventDetail(Long eventId) {

        Event event = eventRepository.findEventDetailById(eventId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện."));
        return mapToDTO(event);
    }

    @Override
    @Transactional
    public void deactivateEvent(Long eventId, String reason) {

        Event  event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện: " + eventId));

        if(event.getStatus() != EventStatus.ACTIVE) {
            throw new RuntimeException("Chỉ có thể tắt sự kiện đang hoạt động.");
        }

        event.setStatus(EventStatus.INACTIVE);
        eventRepository.save(event);

        User organizer = event.getOrganizer();
        try {
            emailService.sendDeactivationEmail(
                    event.getOrganizer().getEmail(),
                    event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName(),
                    event.getTitle(),
                    reason
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void activateEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện."));

        if(event.getStatus() != EventStatus.INACTIVE) {
            throw new RuntimeException("CHỉ có thể bật lại sự kiện đã không hoạt động.");
        }

        event.setStatus(EventStatus.ACTIVE);
        eventRepository.save(event);
    }

    private ModeratorEventDetailDTO mapToDTO(Event event) {
        ModeratorEventDetailDTO dto = new ModeratorEventDetailDTO();
        dto.setEventId(event.getEventId());
        dto.setTitle(event.getTitle());
        dto.setStatus(event.getStatus());
        dto.setThumbnailUrl(event.getThumbnailUrl());
        dto.setCategoryName(event.getCategory().getCategoryName());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setVenueName(event.getVenueName());

        if(event.getAddress() != null) {
            dto.setVenueAddress(event.getAddress().getSpecificAddress());
            if(event.getAddress().getWard() != null) {
                dto.setCityName(event.getAddress().getWard().getCity().getName());
            }
        }

        User organizer = event.getOrganizer();
        dto.setOrganizerId(organizer.getId());
        dto.setOrganizerName(organizer.getFirstName() + " " + organizer.getLastName());
        dto.setOrganizerEmail(organizer.getEmail());
        dto.setOrganizerAvatarUrl(organizer.getAvatar());

        return dto;
    }

}
