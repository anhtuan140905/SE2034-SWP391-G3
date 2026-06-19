package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.OrderDetail;
import vn.edu.fpt.model.Seat;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query("SELECT od.seat.seatId FROM OrderDetail od WHERE od.order.orderId = :orderId")
    List<Long> findSeatIdByOrderId(Long orderId);
}
