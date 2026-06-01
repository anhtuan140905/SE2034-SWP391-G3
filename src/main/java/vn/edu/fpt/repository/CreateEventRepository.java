package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Venue;

import java.time.LocalDate;
import java.util.List;

public interface CreateEventRepository extends JpaRepository<Event,Long> {
    List<Venue> findByDateNot( LocalDate dates);


}
