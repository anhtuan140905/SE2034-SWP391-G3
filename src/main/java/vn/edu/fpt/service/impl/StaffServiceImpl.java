package vn.edu.fpt.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.*;
import vn.edu.fpt.modelview.request.organizer.MemberRequestDTO;
import vn.edu.fpt.modelview.response.organizer.PermissionDTO;
import vn.edu.fpt.modelview.response.organizer.RoleDTO;
import vn.edu.fpt.modelview.response.organizer.StaffDetailDto;
import vn.edu.fpt.modelview.response.organizer.StaffResponceDTO;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.service.StaffService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StaffServiceImpl implements StaffService {
    private PermissionRepository permissionRepository;
    private RoleRepository roleRepository;
    private UserRoleRepository userRoleRepository;
    private UserRepository userRepository;
    private EventRepository eventRepository;
    private OrganizerMemberRepository organizerMemberRepository;
    //        before update
//        kiem tra roleid dang ruoc va rodeid - nguoi update
//        delete
//        het su kien xem no con con userrole nay voi su kien khac khong , new khong thi xoa , neu con thi giu
//        update
//        kiem tra userrole neu chua co thi them moi , neu co thi dung lai voi update
    @Override
    @Transactional
    public void updateStaff(StaffDetailDto dto) {
        OrganizerMember member = organizerMemberRepository
                .findById(dto.getStaffId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy staff"));

        updateRole(member, dto.getRoleId());
        updatePermissions(member, dto.getPermission());
        organizerMemberRepository.save(member);
    }
    // ─── XỬ LÝ ROLE ─────────────────────────────────────────────
    private void updateRole(OrganizerMember member, Long newRoleId) {
        UserRole oldUserRole = member.getUserRole();
        if (oldUserRole.getRole().getId().equals(newRoleId)) {
            return; // không đổi role
        }
        Long userId = oldUserRole.getUser().getId();
        Optional<UserRole> existing = userRoleRepository.findByUserIdAndRoleId(userId, newRoleId);
        if (existing.isPresent()) {
            member.setUserRole(existing.get());
            return;
        }
        long usageCount = organizerMemberRepository
                .countByUserRole_Id(oldUserRole.getId());
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
                                new RuntimeException("Permission không tồn tại: " + id));

                OrganizerMemberPermission omp = new OrganizerMemberPermission();
                omp.setOrganizerMember(member);
                omp.setPermission(permission);
                member.getPermissions().add(omp);
            }
        }
    }
    @Override
    public StaffDetailDto getInfobyStaffID(Long id) {
        OrganizerMember organizerMember = organizerMemberRepository.findById(id).orElseThrow(()->new RuntimeException("Staff not Found"));
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
    public List<PermissionDTO> getListPermission() {
        List<Permission> permissions = permissionRepository.findAll();
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
    public  List<RoleDTO> getRoleOfEvent(){
        List<Role> roles = roleRepository.findRoleMemberOfOrganizer();
        List<RoleDTO> dtos = new ArrayList<>();
        for (Role role:roles){
            RoleDTO dto = new RoleDTO();
            dto.setId(role.getId());
            dto.setName(role.getRoleName().name());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    @Transactional
    public void assignMember(MemberRequestDTO memberRequestDTO,
                            Long EventId) {
        User user = userRepository.findByEmail(memberRequestDTO.getEmail());
//        if user not found which means user not in database
        if(user==null){
            throw new RuntimeException("User Not Found With Email:"+ memberRequestDTO.getEmail());
        }
        boolean alreadyExists = organizerMemberRepository
                .findbyUserIdAndEventId(user.getId(), EventId)
                .isPresent();
        if (alreadyExists) {
            throw new RuntimeException("User này đã có role trong event!");
        }
        UserRole userRole = userRoleRepository.findByUserIdAndRoleId(user.getId(), memberRequestDTO.getRoleId()).orElse(null);
        if(userRole==null){
            userRole = new UserRole();
            userRole.setUser(user);
            Role role = roleRepository.getReferenceById(memberRequestDTO.getRoleId());
            userRole.setRole(role);
            userRoleRepository.save(userRole);
        }
        Event event = eventRepository.findById(EventId).orElseThrow(()->new RuntimeException("Event Not Found With :"+EventId));
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
    public Page<StaffResponceDTO> getStaffbyEventID(Long id, String keyword, Long roleId, Pageable pageable) {
        Page<OrganizerMember> organizerMemberList = organizerMemberRepository.getOrganizerMemberByEventID(id,keyword,roleId, pageable);
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

        return new PageImpl<>(dtos, pageable, organizerMemberList.getTotalElements());
    }

    @Override
    public boolean checkPermission(Long userId, Long eventId, Long permissionId) {
        OrganizerMember organizerMember = organizerMemberRepository.CheckPermission(userId, eventId);
        if (organizerMember == null) {
            return false;
        }
        for (OrganizerMemberPermission omp : organizerMember.getPermissions()) {
            if (omp.getPermission().getId().equals(permissionId)) {
                return true;
            }
        }
        return false;
    }

}




