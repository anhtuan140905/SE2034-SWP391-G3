
package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
    List<Order> findTop10ByEvent_OrganizerIdOrderByCreatedAtDesc(Long userId);

}