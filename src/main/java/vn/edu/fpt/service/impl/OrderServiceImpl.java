package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.User;
import vn.edu.fpt.repository.OrderRepository;
import vn.edu.fpt.service.OrderService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public Order findById(long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order getOrderForCheckout(Long orderId, User currentUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order không tồn tại"));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền xem order này");
        }
        return order;
    }

    @Override
    public Order handleSaveOrder(Order order) {
        return this.orderRepository.save(order);
    }
}
