package vn.edu.fpt.repository;

import java.math.BigDecimal;

public interface OrderProjection {
    Long getOrderId();
    String getFullName();
    String getPhone();
    String getCreateAt();
    String getQuantityTicket();
    BigDecimal getTotalAmount();
    String getStatus();
}