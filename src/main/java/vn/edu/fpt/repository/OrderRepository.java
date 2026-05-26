package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.constant.OrderStatus;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByEventAndStatus(Event event, OrderStatus status);
}
