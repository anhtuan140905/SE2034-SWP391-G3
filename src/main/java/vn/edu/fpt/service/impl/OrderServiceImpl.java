package vn.edu.fpt.service.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.modelview.response.homepage.TicketDTO;
import vn.edu.fpt.modelview.response.organizer.OrderDto;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.repository.OrderProjection;
import vn.edu.fpt.repository.OrderRepository;
import vn.edu.fpt.service.OrderService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final EventRepository eventRepository;
    @Override
    public Page<OrderDto> getOrderbyEventID(Long eventId, String keyword, String status, int page) {
        Sort sort = Sort.by(Sort.Direction.ASC, "Order.orderId");
        Pageable pageable =  PageRequest.of(page,10);
        String kw = (keyword == null || keyword.isBlank()) ? null : "%" + keyword + "%";
        String st = (status == null || status.isBlank()) ? null : status;
        Event event = eventRepository.findById(eventId).orElseThrow(()-> new RuntimeException("Không tìm thấy sự kiện "));
        Page<OrderProjection> projections = orderRepository.findOrderByEventId(eventId,kw, st,pageable);
        List<OrderDto> result = new ArrayList<>();
        for (OrderProjection p : projections) {
            OrderDto dto = new OrderDto();
            dto.setEventTitle(event.getTitle());
            dto.setOrderId(p.getOrderId());
            dto.setFullName(p.getFullName());
            dto.setPhone(p.getPhone());
            dto.setQuantityTicket(p.getQuantityTicket());
            dto.setStatus(p.getStatus());
            dto.setCreateAt(p.getCreateAt());
            dto.setTotalAmount(p.getTotalAmount());
            result.add(dto);
        }
        return new PageImpl<>(result,pageable,projections.getTotalElements());
    }

    @Override
    public Order findById(long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order getOrderForCheckout(Long orderId, User currentUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn Hàng không tồn tại"));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền xem order này");
        }
        return order;
    }

    @Override
    public Order handleSaveOrder(Order order) {
        return this.orderRepository.save(order);
    }

    @Override
    @Transactional
    public void handleUpdateStatusOrder(Long orderId) {
        Order order = this.orderRepository.findById(orderId).orElse(null);
        if(order != null) {
            order.setStatus(OrderStatus.CANCELLED);
            this.orderRepository.save(order);
        }
    }


    @Override
    public List<TicketDTO> viewOrder(Long userId, String tab) {
        List<TicketDTO> allTickets = orderRepository.viewOrder(userId)
                .stream()
                .map(TicketDTO::new)
                .peek(this::applyComputedStatus)
                .toList();

        return switch (tab) {
            case "upcoming" -> allTickets.stream()
                    .filter(t -> "ACTIVE".equals(t.getStatus()))
                    .toList();
            case "used" -> allTickets.stream()
                    .filter(t -> "USED".equals(t.getStatus()))
                    .toList();
            case "expired" -> allTickets.stream()
                    .filter(t -> "EXPIRED".equals(t.getStatus()))
                    .toList();
            default -> allTickets;
        };
    }

    private void applyComputedStatus(TicketDTO t) {
        boolean isCheckedIn = "true".equals(t.getStatus());
        if (isCheckedIn) {
            t.setStatus("USED");
        } else if (t.getEndTime() != null && t.getEndTime().isBefore(LocalDateTime.now())) {
            t.setStatus("EXPIRED");
        } else {
            t.setStatus("ACTIVE");
        }
    }

    public List<TicketDTO> viewOrderDetail(Long orderId) {
        return orderRepository.viewOrderDetail(orderId)
                .stream()
                .map(TicketDTO::new)
                .peek(this::applyComputedStatus)
                .toList();
    }

    @Override
    public List<Event> findPurchasedEventsByUserId(Long userId) {
        return this.orderRepository.findPurchasedEvents(userId, OrderStatus.PAID);
    }
}
