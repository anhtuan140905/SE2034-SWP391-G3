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
public class VenueService {

    private final VenueRepository venueRepository;
    private final CityRepository cityRepository;
    private final WardRepository wardRepository;

    public VenueService(
            VenueRepository venueRepository,
            CityRepository cityRepository,
            WardRepository wardRepository) {
        this.venueRepository = venueRepository;
        this.cityRepository  = cityRepository;
        this.wardRepository  = wardRepository;
    }

    public void createVenue(CreateVenueDTO request) {

        // =====================
        // Tìm City theo ID
        // =====================
        City city = cityRepository.findById(request.getCity())
                .orElseThrow(() -> new RuntimeException(
                        "City not found with id: " + request.getCity()
                ));

        // =====================
        // Tìm Ward theo ID
        // =====================
        Ward ward = wardRepository.findById(request.getWard())
                .orElseThrow(() -> new RuntimeException(
                        "Ward not found with id: " + request.getWard()
                ));

        // =====================
        // Address 
        // =====================
        Address address = new Address();
        address.setCity(city.getName());
        address.setWard(ward.getName());
        address.setStreetAddress(request.getStreetAddress());

        // =====================
        // Venue
        // =====================
        Venue venue = new Venue();
        venue.setVenueName(request.getVenueName());
        venue.setAddress(address);
        venue.setDescription(request.getDescription());
        venue.setImageUrl(request.getImageUrl());

        // =====================
        // Capacity
        // =====================
        int totalCapacity = request.getZones()
                .stream()
                .mapToInt(z -> z.getRows() * z.getSeatsPerRow())
                .sum();
        venue.setCapacity(totalCapacity);

        // =====================
        // Zones + Seats
        // =====================
        List<VenueZone> zoneList = new ArrayList<>();

        for (VenueZoneDTO zoneRequest : request.getZones()) {

            VenueZone zone = new VenueZone();
            zone.setVenue(venue);
            zone.setZoneName(zoneRequest.getZoneName());
            zone.setRows(zoneRequest.getRows());
            zone.setSeatsPerRow(zoneRequest.getSeatsPerRow());

            List<Seat> seats = new ArrayList<>();
            for (int r = 0; r < zoneRequest.getRows(); r++) {
                String rowLabel = String.valueOf((char) ('A' + r));
                for (int s = 1; s <= zoneRequest.getSeatsPerRow(); s++) {
                    Seat seat = new Seat();
                    seat.setZone(zone);
                    seat.setRowLabel(rowLabel);
                    seat.setSeatNumber(s);
                    seats.add(seat);
                }
            }

            zone.setSeats(seats);
            zoneList.add(zone);
        }

        venue.setZones(zoneList);


        // Save

        venueRepository.save(venue);
    }

    public List<Venue> getAllVenue() {
        return venueRepository.findAll();
    }
    public Venue findById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found: " + id));
    }
}