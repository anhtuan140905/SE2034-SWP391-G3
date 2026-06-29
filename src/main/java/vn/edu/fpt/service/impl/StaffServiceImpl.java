package vn.edu.fpt.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.modelview.request.organizer.MemberRequestDTO;
import vn.edu.fpt.modelview.response.organizer.PermissionDTO;
import vn.edu.fpt.modelview.response.organizer.RoleDTO;
import vn.edu.fpt.modelview.response.organizer.StaffResponceDTO;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.service.StaffService;

import java.util.ArrayList;
import java.util.List;
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
        UserRole userRole = userRoleRepository.findByUserIdAndRoleId(user.getId(), memberRequestDTO.getRoleId());
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




