package com.example.mall.controller;

import com.example.mall.model.CartItem;
import com.example.mall.model.Product;
import com.example.mall.service.CartService;
import com.example.mall.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {
    private final CartService cartService;
    private final ProductService productService;

    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> cart(HttpSession session) {
        Long userId = getLoginUserId(session);
        List<CartItem> items = cartService.getCart(userId);
        return ResponseEntity.ok(Map.of("items", items, "totalPrice", cartService.totalPrice(userId)));
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(@Valid @RequestBody AddCartRequest request, HttpSession session) {
        Long userId = getLoginUserId(session);
        Product product = productService.getById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));

        List<CartItem> items = cartService.addToCart(userId, product, request.quantity());
        return ResponseEntity.ok(Map.of("message", "加入购物车成功", "items", items, "totalPrice", cartService.totalPrice(userId)));
    }

    private Long getLoginUserId(HttpSession session) {
        Object userId = session.getAttribute("LOGIN_USER_ID");
        if (userId == null) {
            throw new IllegalStateException("请先登录");
        }
        return (Long) userId;
    }

    public record AddCartRequest(
            @NotNull(message = "商品ID不能为空") Long productId,
            @Min(value = 1, message = "数量至少为1") int quantity
    ) {
    }
}
