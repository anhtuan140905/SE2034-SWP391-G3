package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.model.Event;

import java.time.LocalDate;
import java.util.List;

public interface CreateEventRepository extends JpaRepository<Event,Long> {

}
