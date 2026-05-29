package vn.edu.fpt.service;

import vn.edu.fpt.model.*;
import vn.edu.fpt.modelview.request.auth.RegisterOrgDTO;
public interface VenueService {
    public User handleCreateOrganizer(RegisterOrgDTO dto);
}
