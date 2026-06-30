package vn.edu.fpt.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.model.OrganizerMember;
import vn.edu.fpt.model.OrganizerProfile;
import vn.edu.fpt.modelview.request.organizer.MemberRequestDTO;
import vn.edu.fpt.modelview.response.organizer.PermissionDTO;
import vn.edu.fpt.modelview.response.organizer.RoleDTO;
import vn.edu.fpt.modelview.response.organizer.StaffDetailDto;
import vn.edu.fpt.modelview.response.organizer.StaffResponceDTO;

import java.util.List;

public interface StaffService {
    void updateStaff(StaffDetailDto staffDetailDto);
    StaffDetailDto getInfobyStaffID(Long id);
    List<PermissionDTO> getListPermission();
    List<RoleDTO> getRoleOfEvent();
    void assignMember(MemberRequestDTO memberRequestDTO, Long EventId);
    Page<StaffResponceDTO> getStaffbyEventID(Long eventId, String keyword, Long roleId, Pageable pageable);
    boolean checkPermission(Long userId,Long eventId,Long permissionId);
}
