package vn.edu.fpt.service;

import org.springframework.stereotype.Service;
import vn.edu.fpt.model.Role;
import vn.edu.fpt.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getRoleByName(String name){
        return this.roleRepository.findByRoleName(name);
    }
}
