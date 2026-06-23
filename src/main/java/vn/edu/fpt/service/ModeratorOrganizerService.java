package vn.edu.fpt.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.modelview.request.moderator.CreateOrganizerRequest;
import vn.edu.fpt.modelview.response.moderator.ModeratorOrganizerListDTO;
import vn.edu.fpt.modelview.response.moderator.OrganizerManagementStatsDTO;

public interface ModeratorOrganizerService {

    void createOrganizerAccount(CreateOrganizerRequest request);

    Page<ModeratorOrganizerListDTO> getOrganizers(String keyword, String status, int page, int size);

    OrganizerManagementStatsDTO getOrganizerManagementStats();

}
