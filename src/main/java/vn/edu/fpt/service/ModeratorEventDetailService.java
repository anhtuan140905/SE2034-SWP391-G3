package vn.edu.fpt.service;

import vn.edu.fpt.modelview.response.moderator.ModeratorEventDetailDTO;

public interface ModeratorEventDetailService {

    ModeratorEventDetailDTO getEventDetail(Long eventId);

    void deactivateEvent(Long eventId, String reason);

    void activateEvent(Long eventId);
}
