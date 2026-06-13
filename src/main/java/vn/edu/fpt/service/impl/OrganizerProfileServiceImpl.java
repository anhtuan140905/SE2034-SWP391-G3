package vn.edu.fpt.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.model.OrganizerProfile;
import vn.edu.fpt.repository.OrganizerProfileRepository;
import vn.edu.fpt.service.OrganizerProfileService;

@Service("OrganizerProfileService")
public class OrganizerProfileServiceImpl implements OrganizerProfileService {
    private final OrganizerProfileRepository organizerProfileRepository;

    public OrganizerProfileServiceImpl(OrganizerProfileRepository organizerProfileRepository) {
        this.organizerProfileRepository = organizerProfileRepository;
    }

    public OrganizerProfile handleCreateOrganizerProfile(OrganizerProfile organizerProfile){
        organizerProfileRepository.save(organizerProfile);
        return organizerProfile;
    }

    @Override
    public boolean existsByTaxCode(String taxCode) {
        return this.organizerProfileRepository.existsByTaxCode(taxCode);
    }

    public static class FinanceServiceImpl {
    }
}
