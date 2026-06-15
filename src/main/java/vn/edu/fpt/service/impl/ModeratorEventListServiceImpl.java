package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.response.moderator.EventManagementStatsDTO;
import vn.edu.fpt.modelview.response.moderator.ModeratorEventListDTO;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.service.ModeratorEventListService;

@Service
@RequiredArgsConstructor
public class ModeratorEventListServiceImpl implements ModeratorEventListService {

    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public EventManagementStatsDTO getEventStats() {

        EventManagementStatsDTO stats = new EventManagementStatsDTO();
        stats.setTotalEvents(eventRepository.countAllEvents());
        stats.setEventsToday(eventRepository.countNewEventsToday());
        stats.setInactiveEvents(eventRepository.countEventsByStatus(EventStatus.INACTIVE));
        stats.setEndedThisMonth(eventRepository.countEventsByStatus(EventStatus.ENDED));

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
     public Page<ModeratorEventListDTO> getEvents(EventStatus status, String keyword, Long categoryId, int page, int size) {

        String stringKeyword = (keyword != null && !keyword.isBlank()) ? keyword : null;

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return eventRepository
                .findEventsWithFilterAndSearch(status, stringKeyword, categoryId, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public void deactivateEvent(Long eventId) {

        Event  event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện: " + eventId));

        if(event.getStatus() != EventStatus.ACTIVE) {
            throw new RuntimeException("Chỉ có thể tắt sự kiện đang hoạt động.");
        }

        event.setStatus(EventStatus.INACTIVE);
        eventRepository.save(event);

    }

    private ModeratorEventListDTO mapToDTO(Event event) {

        ModeratorEventListDTO dto = new ModeratorEventListDTO();

        dto.setEventId(event.getEventId());
        dto.setTitle(event.getTitle());
        dto.setOrganizerName(event.getOrganizer().getFirstName() + " "
                + event.getOrganizer().getLastName());
        dto.setStartTime(event.getStartTime());
        dto.setStatus(event.getStatus());

        return dto;
    }
}
