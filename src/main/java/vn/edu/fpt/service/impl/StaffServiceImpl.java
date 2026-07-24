package vn.edu.fpt.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.modelview.request.organizer.MemberRequestDTO;
import vn.edu.fpt.modelview.response.organizer.PermissionDTO;
import vn.edu.fpt.modelview.response.organizer.RoleDTO;
import vn.edu.fpt.modelview.response.organizer.StaffDetailDto;
import vn.edu.fpt.modelview.response.organizer.StaffResponceDTO;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.service.StaffService;

import java.util.*;

@Service
@AllArgsConstructor
public class StaffServiceImpl implements StaffService {
    private PermissionRepository permissionRepository;
    private RoleRepository roleRepository;
    private UserRoleRepository userRoleRepository;
    private UserRepository userRepository;
    private EventRepository eventRepository;
    private OrganizerMemberRepository organizerMemberRepository;


    @Override
    public boolean compareRole(Long userId, Long staffId, Long eventId) {
        OrganizerMember organizerMember = organizerMemberRepository.findbyUserIdAndEventId(userId,eventId)
                                        .orElseThrow(()->new RuntimeException("Bạn Không Phải Nhân viên của sự kiện"));
        String roleUser = organizerMember.getUserRole().getRole().getRoleName().toString();
        if(roleUser.equals(RoleName.ROLE_ORGANIZER.toString())){
            return false;
        }
        String roleStaff = organizerMemberRepository.getReferenceById(staffId).getUserRole().getRole().getRoleName().toString();
        if(roleUser.equals(roleStaff)){
            return true;
        }
        return false;
    }
    @Override
    public void AutodeleteStaffByStaffId(Long staffId,Long eventId,Long userId) {
        UserRole userRole = organizerMemberRepository.getReferenceById(staffId).getUserRole();
        long usageCount = organizerMemberRepository.countByUserRole_Id(userRole.getId());
        if(usageCount>1){
            organizerMemberRepository.deleteById(staffId);
        }else {
            userRoleRepository.deleteById(userRole.getId());
        }
    }
    @Override
    public void deleteStaffByStaffId(Long staffId,Long eventId,Long userId) {
        if(compareRole(userId,staffId,eventId)){
            throw new RuntimeException("Bạn không có quyền xóa nhần viên này");
        }
        UserRole userRole = organizerMemberRepository.getReferenceById(staffId).getUserRole();
        long usageCount = organizerMemberRepository.countByUserRole_Id(userRole.getId());
        if(usageCount>1){
            organizerMemberRepository.deleteById(staffId);
        }else {
            userRoleRepository.deleteById(userRole.getId());
        }
    }
    @Override
    public String getRoleNameByUserId(Long userId) {
        List<UserRole> userRoles = userRoleRepository.finUserByUserId(userId);
        if(userRoles !=null){
        for (UserRole userRole : userRoles) {
            String roleName = userRole.getRole()
                    .getRoleName()
                    .name();
            if (roleName.equals("ROLE_MANAGER")) {
                return roleName;
            }
        }}
        return "";
    }
    //        before update
//        kiem tra roleid dang ruoc va rodeid - nguoi update
//        delete
//        het su kien xem no con con userrole nay voi su kien khac khong , new khong thi xoa , neu con thi giu
//        updateq
//        kiem tra userrole neu chua co thi them moi , neu co thi dung lai voi update
//


    @Override
    @Transactional
    public void updateStaff(Long userId,StaffDetailDto dto,Long eventId) {
        if(compareRole(userId,dto.getStaffId(),eventId)){
            throw new RuntimeException("Bạn không có quyền sửa nhần viên này");
        }
        OrganizerMember member = organizerMemberRepository
                .findById(dto.getStaffId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        updateRole(member, dto.getRoleId());
        updatePermissions(member, dto.getPermission());
        organizerMemberRepository.save(member);
    }
    // ─── XỬ LÝ ROLE ─────────────────────────────────────────────
    private void updateRole(OrganizerMember member, Long newRoleId) {
        UserRole oldUserRole = member.getUserRole();
//        if no change role
        if (oldUserRole.getRole().getId().equals(newRoleId)) {
            return; // no change role
        }
//        if this user had this new role in other event -> use role again
        Long userId = oldUserRole.getUser().getId();
        Optional<UserRole> existing = userRoleRepository.findByUserIdAndRoleId(userId, newRoleId);
        if (existing.isPresent()) {
            member.setUserRole(existing.get());
            long oldUsageCount = organizerMemberRepository.countByUserRole_Id(oldUserRole.getId());
            if (oldUsageCount <= 1) {
                userRoleRepository.delete(oldUserRole);
            }
            return;
        }
//        if only 1 role with 1 event -> set role
//        else add this user with new role
        long usageCount = organizerMemberRepository.countByUserRole_Id(oldUserRole.getId());
        if (usageCount > 1) {
            UserRole newUserRole = new UserRole();
            newUserRole.setUser(oldUserRole.getUser());
            newUserRole.setRole(roleRepository.getReferenceById(newRoleId));
            member.setUserRole(userRoleRepository.save(newUserRole));
        } else {
            oldUserRole.setRole(roleRepository.getReferenceById(newRoleId));
            userRoleRepository.save(oldUserRole);
        }
    }
    // ─── XỬ LÝ PERMISSION (dùng for) ──────────────────────────────
    private void updatePermissions(OrganizerMember member, List<Long> newPermissionIds) {
        Set<Long> oldIds = new HashSet<>();
        for (OrganizerMemberPermission p : member.getPermissions()) {
            oldIds.add(p.getPermission().getId());
        }
        Set<Long> newIds = new HashSet<>(newPermissionIds);
        // Tìm permission cần xóa
        Set<OrganizerMemberPermission> canXoa = new HashSet<>();
        for (OrganizerMemberPermission p : member.getPermissions()) {
            if (!newIds.contains(p.getPermission().getId())) {
                canXoa.add(p);
            }
        }
        member.getPermissions().removeAll(canXoa);
        // Tìm permission cần thêm
        for (Long id : newIds) {
            if (!oldIds.contains(id)) {
                Permission permission = permissionRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Quyền  không tồn tại" ));
                OrganizerMemberPermission omp = new OrganizerMemberPermission();
                omp.setOrganizerMember(member);
                omp.setPermission(permission);
                member.getPermissions().add(omp);
            }
        }
    }
    @Override
    public StaffDetailDto getInfobyStaffID(Long id) {
        OrganizerMember organizerMember = organizerMemberRepository.findById(id).orElseThrow(()->new RuntimeException("Không tìm thấy nhân Viên"));
        StaffDetailDto dto = new StaffDetailDto();
        dto.setStaffId(id);
        String name = organizerMember.getUserRole().getUser().getLastName()
                + organizerMember.getUserRole().getUser().getMiddleName()
                + organizerMember.getUserRole().getUser().getFirstName();
        dto.setFullName(name);
        dto.setEmail(organizerMember.getUserRole().getUser().getEmail());
        dto.setRoleId(organizerMember.getUserRole().getRole().getId());
        List<Long> permisstionIds =  new ArrayList<>();
        for (OrganizerMemberPermission p :organizerMember.getPermissions()){
            permisstionIds.add(p.getPermission().getId());
        }
        dto.setPermission(permisstionIds);
        return dto;
    }

    @Override
    public List<PermissionDTO> getListPermission(Long userId,Long eventId) {
        List<Permission> permissions;
        if(isOrganizer(userId,eventId)){
            permissions = permissionRepository.getAllPermission();
        }else{
            permissions = permissionRepository.getStaffPermission();
        }
        List<PermissionDTO> permissionDTOS = new ArrayList<>();
        for(Permission permission:permissions){
            PermissionDTO dto = new PermissionDTO();
            dto.setId(permission.getId());
            dto.setDescription(permission.getDescription());
            permissionDTOS.add(dto);
        }
        return permissionDTOS;
    }
    @Override
    public  List<RoleDTO> getRoleOfEvent(Long userId,Long eventId){
        List<Role> roles ;
        if(isOrganizer(userId,eventId)){
            roles = roleRepository.findRoleMemberOfOrganizer();
        }else{
            roles = roleRepository.findRoleMemberOfManager();
        }
        List<RoleDTO> dtos = new ArrayList<>();
        for (Role role:roles){
            RoleDTO dto = new RoleDTO();
            dto.setId(role.getId());
            dto.setName(role.getRoleName().name());
            dtos.add(dto);
        }
        return dtos;
    }
    private boolean isOrganizer(Long userId, Long eventId){
        OrganizerMember organizerMember = organizerMemberRepository.findbyUserIdAndEventId(userId,eventId).orElse(null);
        if (organizerMember==null){
            throw new RuntimeException("Không tim thấy người dùng");
        }
        if(organizerMember.getUserRole().getRole().getRoleName()== RoleName.ROLE_ORGANIZER){
            return true;
        }else {
            return false;
        }
    }
    @Override
    @Transactional
    public void assignMember(MemberRequestDTO memberRequestDTO,
                            Long EventId) {
        User user = userRepository.findByEmail(memberRequestDTO.getEmail());
//        if user not found which means user not in database
        if(user==null){
            throw new RuntimeException("Không tìm thấy người Dùng");
        }
        boolean alreadyExists = organizerMemberRepository
                .findbyUserIdAndEventId(user.getId(), EventId)
                .isPresent();
        if (alreadyExists) {
            throw new RuntimeException("Người Dùng  đã có role trong Sự kiên!");
        }
        UserRole userRole = userRoleRepository.findByUserIdAndRoleId(user.getId(), memberRequestDTO.getRoleId()).orElse(null);

        if(userRole==null){
            userRole = new UserRole();
            userRole.setUser(user);
            Role role = roleRepository.getReferenceById(memberRequestDTO.getRoleId());
            userRole.setRole(role);
            userRoleRepository.save(userRole);
        }
        Event event = eventRepository.findById(EventId).orElseThrow(()->new RuntimeException("Không tìm thấy Sự kiện"));
        OrganizerMember orgMember = new OrganizerMember();
        orgMember.setUserRole(userRole);
        orgMember.setEvent(event);
        if (memberRequestDTO.getPermissionId() != null && !memberRequestDTO.getPermissionId().isEmpty()) {
            for (Long pId : memberRequestDTO.getPermissionId()) {
                Permission permission = permissionRepository.getReferenceById(pId);

                OrganizerMemberPermission memberPermission = new OrganizerMemberPermission();
                memberPermission.setOrganizerMember(orgMember);
                memberPermission.setPermission(permission);


                orgMember.getPermissions().add(memberPermission);
            }
        }
        organizerMemberRepository.save(orgMember);
        }

    @Override
    public Page<StaffResponceDTO> getStaffbyEventID(Long id, String keyword, Long roleId,int page) {
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "userRole.role.id");
        Page<OrganizerMember> organizerMemberList = organizerMemberRepository.getOrganizerMemberByEventID(id,keyword,roleId, PageRequest.of(page, size,sort));
        List<StaffResponceDTO> dtos = new ArrayList<>();
        for (OrganizerMember organizerMember : organizerMemberList) {
            StaffResponceDTO dto = new StaffResponceDTO();
            dto.setMemberId(organizerMember.getId());
            String name = organizerMember.getUserRole().getUser().getLastName()
                    + organizerMember.getUserRole().getUser().getMiddleName()
                    + organizerMember.getUserRole().getUser().getFirstName();
            dto.setName(name);
            dto.setEmail(organizerMember.getUserRole().getUser().getEmail());
            dto.setRole(organizerMember.getUserRole().getRole().getRoleName().name());
            dtos.add(dto);
        }

        return new PageImpl<>(dtos, PageRequest.of(page, size,sort), organizerMemberList.getTotalElements());
    }

    @Override
    public boolean checkPermission(Long userId, Long eventId, String  permissionKey) {
        OrganizerMember organizerMember = organizerMemberRepository.CheckPermission(userId, eventId);
        if (organizerMember == null) {
            return false;
        }
        for (OrganizerMemberPermission omp : organizerMember.getPermissions()) {
            if (omp.getPermission().getPermissionKey().equals(permissionKey)) {
                return true;
            }
        }
        return false;
    }

}




