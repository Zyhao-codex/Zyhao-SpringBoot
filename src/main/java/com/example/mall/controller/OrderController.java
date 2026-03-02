package com.example.mall.controller;

import com.example.mall.model.CartItem;
import com.example.mall.model.Order;
import com.example.mall.service.CartService;
import com.example.mall.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;

    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(HttpSession session) {
        Long userId = getLoginUserId(session);
        List<CartItem> cartItems = cartService.getCart(userId);
        Order order = orderService.createOrder(userId, cartItems, cartService.totalPrice(userId));
        cartService.clearCart(userId);
        return ResponseEntity.ok(Map.of("message", "下单成功，订单进入待付款状态", "order", order));
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Map<String, Object>> payOrder(@PathVariable Long orderId, HttpSession session) {
        Long userId = getLoginUserId(session);
        Order order = orderService.payOrder(userId, orderId);
        return ResponseEntity.ok(Map.of("message", "支付成功", "order", order));
    }

    @PostMapping("/{orderId}/refund")
    public ResponseEntity<Map<String, Object>> refundOrder(@PathVariable Long orderId, HttpSession session) {
        Long userId = getLoginUserId(session);
        Order order = orderService.refundOrder(userId, orderId);
        return ResponseEntity.ok(Map.of("message", "退款成功，库存已回退", "order", order));
    }

    @GetMapping
    public List<Order> listOrders(HttpSession session) {
        Long userId = getLoginUserId(session);
        return orderService.listOrders(userId);
    }

    private Long getLoginUserId(HttpSession session) {
        Object userId = session.getAttribute("LOGIN_USER_ID");
        if (userId == null) {
            throw new IllegalStateException("请先登录");
        }
        return (Long) userId;
    }
}
