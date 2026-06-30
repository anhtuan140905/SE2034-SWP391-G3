package vn.edu.fpt.modelview.response.homepage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.edu.fpt.repository.TicketProjection;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TicketDTO {
    private Long ticketId;
    private Long orderId;
    private String eventName;
    private String categoryName;
    private String thumbnailUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String specificAddress;
    private String wardName;
    private String cityName;
    private String zoneName;
    private String organizer;
    private String type;
    private Long quantity;
    private Boolean checkedIn;
    private String status;
    private String price;
    private String purchase;
    private String seat;
    private String section;
    private String row;
    private String ticketCount;
    private String ticketCode;
    private String qrCode;
    private String createdAt;
    private String paymentCode;


    public TicketDTO(TicketProjection projection) {
        this.ticketId = projection.getTicketId();
        this.orderId = projection.getOrderId();
        this.eventName = projection.getEventName();
        this.categoryName = projection.getCategoryName();
        this.thumbnailUrl = projection.getThumbnailUrl();
        this.startTime = projection.getStartTime();
        this.endTime = projection.getEndTime();
        this.specificAddress = projection.getSpecificAddress();
        this.wardName = projection.getWardName();
        this.cityName = projection.getCityName();
        this.zoneName = projection.getZoneName();
       this.organizer = projection.getOrganizer();
       this.type = projection.getType();
       this.quantity = projection.getQuantity();
       this.checkedIn = projection.getCheckedIn();
       this.status = projection.getStatus();
       this.price = projection.getPrice();
       this.purchase = projection.getPurchase();
       this.seat = projection.getSeat();
       this.section = projection.getSection();
       this.row = projection.getRow();
       this.ticketCount = projection.getTicketCount();
       this.ticketCode = projection.getTicketCode();
       this.qrCode = projection.getQrCode();
       this.createdAt = projection.getCreatedAt();
       this.paymentCode = projection.getPaymentCode();

    }
}
