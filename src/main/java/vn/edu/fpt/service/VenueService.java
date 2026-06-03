package vn.edu.fpt.service;

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
    public void createVenue(CreateVenueDTO request);
    public List<Venue> getAllVenue();
    public Venue findById(Long id);
    public List<Venue> searchVenue(String keyword);


}