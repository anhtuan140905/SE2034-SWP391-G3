
package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.constant.OrderStatus;

import java.math.BigDecimal;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.event = :event
          AND o.status = :status
    """)
    BigDecimal sumTotalAmountByEventAndStatus(@Param("event") Event event,
                                              @Param("status") OrderStatus status);


}
