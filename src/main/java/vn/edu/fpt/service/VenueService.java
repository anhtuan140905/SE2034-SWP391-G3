package vn.edu.fpt.service;

import org.springframework.stereotype.Service;
import vn.edu.fpt.model.Address;
import vn.edu.fpt.model.Seat;
import vn.edu.fpt.model.Venue;
import vn.edu.fpt.model.VenueZone;
import vn.edu.fpt.modelview.request.admin.CreateVenueDTO;
import vn.edu.fpt.modelview.request.admin.VenueZoneDTO;
import vn.edu.fpt.repository.VenueRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public void createVenue(CreateVenueDTO request) {

//        String currentUser = SecurityContextHolder.getContext()
//                .getAuthentication().getName();

        // 1. Tạo Venue
        Venue venue = new Venue();
        venue.setVenueName(request.getVenueName());
        String streetAddress = request.getStreetAddress();
        String city = request.getCity();
        String ward = request.getWard();
        Address address = new Address();
        address.setStreetAddress(streetAddress);
        address.setCity(city);
        address.setWard(ward);
        venue.setAddress(address);
        venue.setDescription(request.getDescription());
        venue.setImageUrl(request.getImageUrl());

        // 2. Tính capacity từ zones
        int totalCapacity = request.getZones().stream()
                .mapToInt(z -> z.getRows() * z.getSeatsPerRow())
                .sum();
        venue.setCapacity(totalCapacity);

        // 3. Tạo VenueZone + generate Seat
        List<VenueZone> zoneList = new ArrayList<>();

        for (VenueZoneDTO zoneRequest : request.getZones()) {
            VenueZone zone = new VenueZone();
            zone.setVenue(venue);
            zone.setZoneName(zoneRequest.getZoneName());
            zone.setRows(zoneRequest.getRows());
            zone.setSeatsPerRow(zoneRequest.getSeatsPerRow());

            // Generate Seat: A1..A10, B1..B10...
            List<Seat> seats = new ArrayList<>();
            for (int r = 0; r < zoneRequest.getRows(); r++) {
                String rowLabel = String.valueOf((char) ('A' + r)); // A, B, C...
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

        // 4. Save — CascadeType.ALL tự save Zone + Seat
        venueRepository.save(venue);
    }
}
