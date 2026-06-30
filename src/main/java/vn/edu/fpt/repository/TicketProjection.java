package vn.edu.fpt.repository;

import java.time.LocalDateTime;

public interface TicketProjection {
     Long getOrderId();
     Long getTicketId();
     String getEventName();
     String getCategoryName();
     String getThumbnailUrl();
     LocalDateTime getStartTime();
     LocalDateTime getEndTime();
     String getSpecificAddress();
     String getWardName();
     String getCityName();
     String getZoneName();
     String getOrganizer();
     String getTime();
     String getType();
     Long getQuantity();
     Boolean getCheckedIn();
     String getStatus();
     String getPrice();
     String getPurchase();
     String getSeat();
     String getSection();
     String getRow();
     String getTicketCount();
     String getTicketCode();
     String getQrCode();
     String getCreatedAt();
     String getPaymentCode();
}
