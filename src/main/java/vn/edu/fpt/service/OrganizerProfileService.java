package vn.edu.fpt.service;

import org.springframework.stereotype.Service;
import vn.edu.fpt.model.OrganizerProfile;
import vn.edu.fpt.repository.OrganizerProfileRepository;

@Service
public class OrganizerProfileService {
    private final OrganizerProfileRepository organizerProfileRepository;

    public OrganizerProfileService(OrganizerProfileRepository organizerProfileRepository) {
        this.organizerProfileRepository = organizerProfileRepository;
    }

    public OrganizerProfile handleCreateOrganizerProfile(OrganizerProfile organizerProfile){
        organizerProfileRepository.save(organizerProfile);
        return organizerProfile;
    }
}










