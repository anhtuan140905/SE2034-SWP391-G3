package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Venue;

@Repository
public interface VenueRepository extends JpaRepository<Venue,Long> {
}
