package vn.edu.fpt.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.modelview.response.homepage.TicketDTO;
import vn.edu.fpt.modelview.response.organizer.OrderDto;

import java.util.List;

public interface OrderService {
    Page<OrderDto> getOrderbyEventID(Long eventId, String keyword, String status, int page);
    public Order findById(long id);
    public Order getOrderForCheckout(Long orderId, User currentUser);
    public Order handleSaveOrder(Order order);
    public void handleUpdateStatusOrder(Long orderId);
    List<Event> findPurchasedEventsByUserId(Long userId);
    boolean existsByVoucher_VoucherIdAndUserIdAndStatus(Long voucherId, Long userId, OrderStatus orderStatus);
}
