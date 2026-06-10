package vn.edu.fpt.service;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.Address;
import vn.edu.fpt.model.City;
import vn.edu.fpt.model.Seat;
import vn.edu.fpt.model.Venue;
import vn.edu.fpt.model.VenueZone;
import vn.edu.fpt.model.Ward;
import vn.edu.fpt.modelview.request.admin.CreateVenueDTO;
import vn.edu.fpt.modelview.request.admin.VenueZoneDTO;
import vn.edu.fpt.repository.CityRepository;
import vn.edu.fpt.repository.VenueRepository;
import vn.edu.fpt.repository.WardRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public interface VenueService {
    void createVenue(CreateVenueDTO request);
    void updateVenue(Long id, CreateVenueDTO request);

    List<Venue> getAllVenue();

    Venue findById(Long id);

    List<Venue> searchVenue(String keyword);

    List<VenueZone> findByVenueVenueId(Long id);


}