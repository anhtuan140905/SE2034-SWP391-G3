package vn.edu.fpt.service.impl;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.VenueStatus;
import vn.edu.fpt.modelview.request.admin.CreateVenueDTO;
import vn.edu.fpt.modelview.request.admin.VenueZoneDTO;
import vn.edu.fpt.repository.CityRepository;
import vn.edu.fpt.repository.VenueRepository;
import vn.edu.fpt.repository.VenueZoneRepository;
import vn.edu.fpt.repository.WardRepository;
import vn.edu.fpt.service.UserService;
import vn.edu.fpt.service.VenueService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service("VenueService")
public class VenueServiceImpl implements VenueService {
    private final VenueRepository venueRepository;
    private final CityRepository cityRepository;
    private final WardRepository wardRepository;
    private final VenueZoneRepository venueZoneRepository;

    public VenueServiceImpl(
            VenueRepository venueRepository,
            CityRepository cityRepository,
            WardRepository wardRepository, VenueZoneRepository venueZoneRepository) {
        this.venueRepository = venueRepository;
        this.cityRepository = cityRepository;
        this.wardRepository = wardRepository;
        this.venueZoneRepository = venueZoneRepository;
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

        address.setWard(ward);
        address.setSpecificAddress(request.getStreetAddress());

        // =====================
        // Venue
        // =====================
        Venue venue = new Venue();
        venue.setVenueName(request.getVenueName());
        venue.setAddress(address);
        venue.setDescription(request.getDescription());
        venue.setImageUrl(request.getImageUrl());
        venue.setStatus(VenueStatus.ACTIVE);
        venue.setStatus(VenueStatus.INACTIVE);
        venue.setStatus(VenueStatus.MAINTAIN);


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

    public List<Venue> searchVenue(String keyword) {
        return venueRepository.findByVenueNameContainingIgnoreCase(keyword);
    }


    public List<VenueZone> findByVenueVenueId(@Param("venueId") Long venueId) {
        return venueZoneRepository.findByVenueVenueId(venueId);
    }


    public void updateVenue(Long id, CreateVenueDTO request) {

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found"));

        Ward ward = wardRepository.findById(request.getWard())
                .orElseThrow(() -> new RuntimeException("Ward not found"));

        venue.setVenueName(request.getVenueName());
        venue.setDescription(request.getDescription());
        venue.getAddress().setWard(ward);
        venue.getAddress().setSpecificAddress(request.getStreetAddress());
        venue.setStatus(request.getStatus());

        if (request.getImageUrl() != null) {
            venue.setImageUrl(request.getImageUrl());
        }


        List<VenueZoneDTO> dtoList = request.getZones();

        if (venue.getZones() == null) {
            venue.setZones(new ArrayList<>());
        }

        List<VenueZone> oldZones = new ArrayList<>(venue.getZones());

        for (int i = 0; i < dtoList.size(); i++) {
            VenueZoneDTO dto = dtoList.get(i);
            VenueZone zone;

            if (i < oldZones.size()) {
                zone = oldZones.get(i);
                zone.setZoneName(dto.getZoneName());
                zone.setRows(dto.getRows());
                zone.setSeatsPerRow(dto.getSeatsPerRow());

                if (zone.getSeats() == null) {
                    zone.setSeats(new ArrayList<>());
                } else {
                    zone.getSeats().clear();
                }
            } else {
                zone = new VenueZone();
                zone.setVenue(venue);
                zone.setZoneName(dto.getZoneName());
                zone.setRows(dto.getRows());
                zone.setSeatsPerRow(dto.getSeatsPerRow());
                zone.setSeats(new ArrayList<>());
                venue.getZones().add(zone);
            }

            for (int r = 0; r < dto.getRows(); r++) {
                String rowLabel = String.valueOf((char) ('A' + r));
                for (int s = 1; s <= dto.getSeatsPerRow(); s++) {
                    Seat seat = new Seat();
                    seat.setZone(zone);
                    seat.setRowLabel(rowLabel);
                    seat.setSeatNumber(s);
                    zone.getSeats().add(seat);
                }
            }
        }

        if (oldZones.size() > dtoList.size()) {
            List<VenueZone> toRemove = oldZones.subList(dtoList.size(), oldZones.size());
            venue.getZones().removeAll(toRemove);
        }

        int totalCapacity = dtoList.stream()
                .mapToInt(dto -> dto.getRows() * dto.getSeatsPerRow())
                .sum();
        venue.setCapacity(totalCapacity);

        venueRepository.save(venue);
    }
}