package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.Payment;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder_OrderId(Long orderId);
    Optional<Payment> findByPaymentCode(String paymentCode);
    Optional<Payment> findByVnpTxnRef(String vnpTxnRef);
}
