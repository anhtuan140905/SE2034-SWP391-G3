package vn.edu.fpt.service;

import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.User;

public interface OrderService {
    public Order findById(long id);
    public Order getOrderForCheckout(Long orderId, User currentUser);
    public Order handleSaveOrder(Order order);
    public void handleUpdateStatusOrder(Long orderId);
}
