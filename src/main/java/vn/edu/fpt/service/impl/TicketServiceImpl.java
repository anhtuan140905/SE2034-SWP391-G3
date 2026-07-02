package vn.edu.fpt.service.impl;

import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import vn.edu.fpt.common.QrCodeUtil;
import vn.edu.fpt.model.*;
import vn.edu.fpt.modelview.response.booking.OrderEmailDTO;
import vn.edu.fpt.modelview.response.booking.TicketEmailDTO;
import vn.edu.fpt.modelview.response.homepage.TicketDTO;
import vn.edu.fpt.repository.SeatLockRepository;
import vn.edu.fpt.repository.TicketProjection;
import vn.edu.fpt.repository.TicketRepository;
import vn.edu.fpt.repository.TicketTypeRepository;
import vn.edu.fpt.service.TicketService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("TicketService")
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final SeatLockRepository  seatLockRepository;
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService;
    @Override
    public long issuedTickets() {
        return this.ticketRepository.ticketIssued();
    }

    @Override
    public void generateTicketsForOrder(Order order) {
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Seat seat = orderDetail.getSeat();

            String ticketCode = "TKT-" + UUID.randomUUID().toString().toUpperCase();
            String qrCode = "EVH-QR-" +  UUID.randomUUID().toString().toUpperCase();

            Ticket ticket = new Ticket();
            ticket.setTicketCode(ticketCode);
            ticket.setOrderDetail(orderDetail);
            ticket.setSeat(seat);
            ticket.setQrCode(qrCode);
            ticket.setCheckedIn(false);
            ticket.setCheckedInAt(null);
            this.ticketRepository.save(ticket);

            TicketType ticketType = seat.getTicketType();
            ticketType.setSoldQuantity(ticketType.getSoldQuantity() + 1);
            ticketTypeRepository.save(ticketType);
        }
    }

    @Override
    public Ticket handleSaveTicket(Ticket ticket) {
        return null;
    }

    @Override
    public void handleSaveTicketByOrder(Order order) {
        List<Long> seatIds = new ArrayList<>();
        List<Ticket> tickets = new ArrayList<>();
        for (OrderDetail detail : order.getOrderDetails()) {
            Seat seat = detail.getSeat();
            if (seat != null) {
                seatIds.add(seat.getSeatId());
                String ticketCode = "EVH-TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                // QR encode đúng ticketCode này — phải khớp với cái lưu DB
                byte[] qrPng = QrCodeUtil.generateQrPng(ticketCode, 300);
                String qrUrl = cloudinaryService.uploadBytes(qrPng, "eventhub/tickets/qr");

                Ticket ticket = Ticket.builder()
                        .seat(seat)
                        .ticketCode(ticketCode)   // dùng lại đúng biến, không gọi UUID mới
                        .qrCode(qrUrl)
                        .isCheckedIn(false)
                        .build();
                ticket.setOrderDetail(detail);
                tickets.add(this.ticketRepository.save(ticket));
            }
            TicketType ticketType = seat.getTicketType();
            ticketType.setSoldQuantity(ticketType.getSoldQuantity() + 1);
            this.ticketTypeRepository.save(ticketType);
        }
        List<TicketEmailDTO> ticketDTOs = tickets.stream()
                .map(t -> new TicketEmailDTO(
                        t.getOrderDetail().getSeat().getTicketType().getEvent().getTitle(),
                        t.getOrderDetail().getSeat().getRowLabel() + t.getOrderDetail().getSeat().getSeatNumber(),
                        t.getTicketCode(),
                        t.getQrCode()
                ))
                .toList();
        OrderEmailDTO orderDTO = new OrderEmailDTO(
                order.getOrderId(),
                order.getUser().getFirstName() + " " +  order.getUser().getMiddleName() + " " + order.getUser().getLastName(),
                order.getUser().getEmail(),
                order.getTotalAmount(),
                ticketDTOs
        );
        this.emailService.sendTicketConfirmationEmail(orderDTO, ticketDTOs);
        if (!seatIds.isEmpty()) {
            seatLockRepository.deleteAllBySeatIdIn(seatIds);
        }
    }

    public long countAllSoldTicket(){
        return ticketRepository.countAllSoldTickets();
    }

    public long countAllTicketOfUser(@Param("userId") Long userId){
        return ticketRepository.countAllTicketOfUser(userId);
    }

    public  long countUpcomingTicket(@Param("userId") Long userId){
        return ticketRepository.countUpcomingTicket(userId);
    }

    public  long countUsedTicket(@Param("userId") Long userId){

        return ticketRepository.countUsedTicket(userId);
    }

    public  long countExpiredTicket(@Param("userId") Long userId){
        return ticketRepository.countExpiredTicket(userId);
    }

    public Ticket findById(Long orderId) {
        return ticketRepository.findById(orderId).orElse(null);
    }


    public List<TicketDTO> viewTicket (Long userId, String tab){
        List<TicketDTO> allTickets = ticketRepository.viewTicket(userId).stream().map(TicketDTO::new).peek(this::applyComputeStatus).toList();
        return switch (tab){
            case "upcoming" -> allTickets.stream().filter(t -> "ACTIVE".equals(t.getStatus())).toList();
            case "used" -> allTickets.stream().filter(t -> "USED".equals(t.getStatus())).toList();
            case "expired" -> allTickets.stream().filter(t -> "EXPIRED".equals(t.getStatus())).toList();
            default -> allTickets;
        };
    }

    private void applyComputeStatus (TicketDTO t){
        boolean isCheckIn = Boolean.TRUE.equals(t.getCheckedIn());
        if(isCheckIn){
            t.setStatus("USED");
        } else if (t.getEndTime() != null && t.getEndTime().isBefore(LocalDateTime.now())) {
            t.setStatus("EXPIRED");

        }
        else{
            t.setStatus("ACTIVE");
        }
    }

    public TicketDTO viewDetailTicket(Long ticketId){
        TicketProjection projection = ticketRepository.viewDetailTicket(ticketId);
        TicketDTO dto = new TicketDTO(projection);
        applyComputeStatus(dto);
        return dto;

    }

}
