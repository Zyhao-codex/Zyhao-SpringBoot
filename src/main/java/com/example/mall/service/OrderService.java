package com.example.mall.service;

import com.example.mall.model.CartItem;
import com.example.mall.model.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {
    private static final String STATUS_PENDING_PAYMENT = "PENDING_PAYMENT";
    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_REFUNDED = "REFUNDED";

    private final AtomicLong orderIdGenerator = new AtomicLong(1);
    private final Map<Long, List<Order>> ordersByUserId = new ConcurrentHashMap<>();
    private final ProductService productService;

    public OrderService(ProductService productService) {
        this.productService = productService;
    }

    public Order createOrder(Long userId, List<CartItem> items, BigDecimal totalPrice) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("购物车为空，无法下单");
        }

        List<CartItem> snapshot = items.stream()
                .map(item -> new CartItem(item.getProduct(), item.getQuantity()))
                .toList();

        productService.deductStock(snapshot);

        Order order = new Order(
                orderIdGenerator.getAndIncrement(),
                userId,
                snapshot,
                totalPrice,
                LocalDateTime.now(),
                STATUS_PENDING_PAYMENT
        );

        ordersByUserId.computeIfAbsent(userId, key -> new ArrayList<>()).add(order);
        return order;
    }

    public Order payOrder(Long userId, Long orderId) {
        Order order = getOrder(userId, orderId);
        if (!STATUS_PENDING_PAYMENT.equals(order.getStatus())) {
            throw new IllegalArgumentException("只有待付款订单可以支付");
        }
        order.setStatus(STATUS_PAID);
        return order;
    }

    public Order refundOrder(Long userId, Long orderId) {
        Order order = getOrder(userId, orderId);
        if (STATUS_REFUNDED.equals(order.getStatus())) {
            throw new IllegalArgumentException("订单已退款");
        }
        if (!STATUS_PAID.equals(order.getStatus()) && !STATUS_PENDING_PAYMENT.equals(order.getStatus())) {
            throw new IllegalArgumentException("当前订单状态不支持退款");
        }
        productService.restoreStock(order.getItems());
        order.setStatus(STATUS_REFUNDED);
        return order;
    }

    public List<Order> listOrders(Long userId) {
        return ordersByUserId.getOrDefault(userId, new ArrayList<>());
    }

    private Order getOrder(Long userId, Long orderId) {
        return ordersByUserId.getOrDefault(userId, new ArrayList<>()).stream()
                .filter(order -> order.getId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
    }
}
