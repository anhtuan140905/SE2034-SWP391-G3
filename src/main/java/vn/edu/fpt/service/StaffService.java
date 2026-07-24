package vn.edu.fpt.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.model.OrganizerMember;
import vn.edu.fpt.model.OrganizerProfile;
import vn.edu.fpt.model.UserRole;
import vn.edu.fpt.modelview.request.organizer.MemberRequestDTO;
import vn.edu.fpt.modelview.response.organizer.PermissionDTO;
import vn.edu.fpt.modelview.response.organizer.RoleDTO;
import vn.edu.fpt.modelview.response.organizer.StaffDetailDto;
import vn.edu.fpt.modelview.response.organizer.StaffResponceDTO;

import java.util.List;

public interface StaffService {
    boolean compareRole(Long userId,Long staffId, Long eventId);
    void AutodeleteStaffByStaffId(Long staffId,Long eventId,Long userId);
    void deleteStaffByStaffId(Long staffId,Long eventId,Long userId);
    String getRoleNameByUserId(Long userId);
    void updateStaff(Long userId,StaffDetailDto staffDetailDto,Long eventId);
    StaffDetailDto getInfobyStaffID(Long id);
    List<PermissionDTO> getListPermission(Long userId,Long eventId);
    List<RoleDTO> getRoleOfEvent(Long userId,Long eventId);
    void assignMember(MemberRequestDTO memberRequestDTO, Long EventId);
    Page<StaffResponceDTO> getStaffbyEventID(Long id, String keyword, Long roleId,int page);
    boolean checkPermission(Long userId,Long eventId, String  permissionKey);
}
