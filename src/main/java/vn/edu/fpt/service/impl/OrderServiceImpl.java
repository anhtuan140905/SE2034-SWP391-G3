package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.constant.OrderStatus;
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

    @Override
    @Transactional
    public void handleUpdateStatusOrder(Long orderId) {
        Order order = this.orderRepository.findById(orderId).orElse(null);
        if(order != null) {
            order.setStatus(OrderStatus.CANCELLED);
            this.orderRepository.save(order);
        }
    }
}
