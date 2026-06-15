package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.response.moderator.DashboardEventDTO;
import vn.edu.fpt.modelview.response.moderator.DashboardStatsDTO;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.repository.UserRepository;
import vn.edu.fpt.service.ModeratorDashboardService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModeratorDashboardServiceImpl implements ModeratorDashboardService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setActiveOrganizers(userRepository.countActiveOrganizers());
        stats.setActiveEvents(eventRepository.countEventsByStatus(EventStatus.ACTIVE));
        stats.setNewEventsToday(eventRepository.countNewEventsToday());
        stats.setInactiveEvents(eventRepository.countEventsByStatus(EventStatus.INACTIVE));
        return stats;
    }

    @Override
    public List<DashboardEventDTO> getRecentEvents() {
        return eventRepository
                .findTopFiveNewEventsToday(PageRequest.of(0, 5))
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<DashboardEventDTO> getTodayEvents() {
        return eventRepository
                .findTopFiveEventsToday(EventStatus.ACTIVE, PageRequest.of(0, 5))
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private DashboardEventDTO mapToDTO(Event event) {
        DashboardEventDTO dto = new DashboardEventDTO();
        dto.setEventId(event.getEventId());
        dto.setTitle(event.getTitle());
        dto.setStartTime(event.getStartTime());
        return dto;
    }
}