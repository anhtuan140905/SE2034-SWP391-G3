package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.TimeLineEvent;

import java.util.List;

@Repository
public interface TimeLineRepository extends JpaRepository<TimeLineEvent, Long> {
    List<TimeLineEvent> findByEvent_EventIdOrderByTimeAsc(Long eventId);
}
