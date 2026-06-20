package vn.edu.fpt.service;

import org.springframework.data.repository.query.Param;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.User;
import vn.edu.fpt.modelview.response.homepage.TicketDTO;
import vn.edu.fpt.repository.TicketProjection;

import java.util.List;

public interface OrderService {
    public Order findById(long id);
    public Order getOrderForCheckout(Long orderId, User currentUser);
    public Order handleSaveOrder(Order order);
    public void handleUpdateStatusOrder(Long orderId);

    List<TicketDTO> viewOrder(Long userId,String tab);
    List<TicketDTO> viewOrderDetail(Long orderId);
}
