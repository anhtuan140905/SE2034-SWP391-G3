package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.model.Venue;
import vn.edu.fpt.model.VenueZone;

import java.util.List;

public interface VenueZoneRepository extends JpaRepository<VenueZone,Long> {
    @Query("SELECT vz FROM VenueZone vz WHERE vz.venue.venueId = :venueId")
    List<VenueZone> findByVenueVenueId(@Param("venueId") Long venueId);



}
