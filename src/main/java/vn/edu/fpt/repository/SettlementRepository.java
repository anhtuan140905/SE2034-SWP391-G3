package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Settlement;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long>, JpaSpecificationExecutor<Event> {


}
