package vn.edu.fpt.service;

import org.springframework.data.domain.Page;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.response.moderator.EventManagementStatsDTO;
import vn.edu.fpt.modelview.response.moderator.ModeratorEventListDTO;

public interface ModeratorEventListService {

    EventManagementStatsDTO getEventStats();

    Page<ModeratorEventListDTO> getEvents(EventStatus status, String keyword, Long categoryId, int page, int size);

    void deactivateEvent(Long eventId);

}
