
package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.constant.OrderStatus;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Dùng bởi CheckoutPageController — eager load details + tickets
    @Query("""
    SELECT o FROM Order o
    LEFT JOIN FETCH o.orderDetails od
    LEFT JOIN FETCH od.seat
    LEFT JOIN FETCH od.ticket
    WHERE o.orderId = :orderId
    """)
    Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);

    List<Order> findByStatusAndExpiresAtBefore(OrderStatus status, Instant now);
    List<Order> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
    List<Order> findTop10ByEvent_OrganizerIdOrderByCreatedAtDesc(Long userId);

    void deleteOrderByOrderIdAndUser(Long orderId, User currentUser);
}
