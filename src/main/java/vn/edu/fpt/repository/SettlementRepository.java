package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Settlement;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.constant.SettlementStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    Optional<Settlement> findByEvent(Event event);
    boolean existsByEvent(Event event);
    List<Settlement> findByStatus(SettlementStatus status);
    List<Settlement> findAllByOrderByCreatedAtDesc();
}
