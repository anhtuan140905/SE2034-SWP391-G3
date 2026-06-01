package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Venue;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue,Long> {
    @Query("SELECT v FROM Venue v WHERE v.id NOT IN (" +
            "  SELECT e.venue.id FROM Event e " +
            "  WHERE CAST(e.date AS date) = :targetDate" +
            ")")
    List<Venue> findAvailableVenuesByDate(@Param("targetDate") LocalDate targetDate);

    List<Venue> findByVenueNameContainingIgnoreCase(String keyword);
}
