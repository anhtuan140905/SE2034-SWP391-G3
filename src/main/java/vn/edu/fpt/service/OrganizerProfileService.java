package vn.edu.fpt.service;

import org.springframework.stereotype.Service;
import vn.edu.fpt.model.OrganizerProfile;
import vn.edu.fpt.repository.OrganizerProfileRepository;


public interface OrganizerProfileService {
    boolean existsByTaxCode(String taxCode);
}










