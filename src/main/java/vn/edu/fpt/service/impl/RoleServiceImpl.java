package vn.edu.fpt.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.model.Role;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.repository.RoleRepository;
import vn.edu.fpt.service.RoleService;

@Service("RoleService")
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getRoleByName(RoleName name){
        return this.roleRepository.findByRoleName(name);
    }


}
