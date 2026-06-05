package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Venue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VenueRepository extends JpaRepository<Venue,Long> {
    @Query("SELECT v FROM Venue v WHERE v.venueId NOT IN (" +
            "  SELECT e.venue.venueId FROM Event e " +
            "  WHERE CAST(e.date AS date) = :targetDate" +
            ")")
    List<Venue> findAvailableVenuesByDate(@Param("targetDate") LocalDate targetDate);
    Optional<Venue> findByVenueId(Long venueId);
    List<Venue> findByVenueNameContainingIgnoreCase(String keyword);
}
