package com.example.mall.service;

import com.example.mall.model.CartItem;
import com.example.mall.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CartService {
    private final Map<Long, List<CartItem>> cartsByUserId = new ConcurrentHashMap<>();

    public List<CartItem> getCart(Long userId) {
        return cartsByUserId.getOrDefault(userId, new ArrayList<>());
    }

    public List<CartItem> addToCart(Long userId, Product product, int quantity) {
        List<CartItem> cart = cartsByUserId.computeIfAbsent(userId, key -> new ArrayList<>());
        CartItem existing = cart.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existing == null) {
            cart.add(new CartItem(product, quantity));
        } else {
            existing.setQuantity(existing.getQuantity() + quantity);
        }
        return cart;
    }

    public void clearCart(Long userId) {
        cartsByUserId.remove(userId);
    }

    public BigDecimal totalPrice(Long userId) {
        return getCart(userId).stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
