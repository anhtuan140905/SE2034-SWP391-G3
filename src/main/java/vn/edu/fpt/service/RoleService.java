package vn.edu.fpt.service;

import org.springframework.stereotype.Service;
import vn.edu.fpt.model.Role;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.repository.RoleRepository;

@Service
public interface RoleService {
    public Role getRoleByName(RoleName name);

}
