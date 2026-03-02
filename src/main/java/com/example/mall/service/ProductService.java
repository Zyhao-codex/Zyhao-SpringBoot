package com.example.mall.service;

import com.example.mall.model.CartItem;
import com.example.mall.model.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductService {
    private final Map<Long, Product> products = new ConcurrentHashMap<>();

    @PostConstruct
    public void initProducts() {
        products.put(1L, new Product(1L, "机械键盘", "RGB 背光机械键盘", new BigDecimal("299.00"), 30));
        products.put(2L, new Product(2L, "无线鼠标", "人体工学无线鼠标", new BigDecimal("129.00"), 50));
        products.put(3L, new Product(3L, "显示器", "27寸 2K 高清显示器", new BigDecimal("1499.00"), 20));
    }

    public Collection<Product> listProducts() {
        return products.values();
    }

    public Optional<Product> getById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    public synchronized void deductStock(List<CartItem> items) {
        for (CartItem item : items) {
            Product product = products.get(item.getProduct().getId());
            if (product == null) {
                throw new IllegalArgumentException("商品不存在");
            }
            if (product.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException(product.getName() + " 库存不足");
            }
        }

        for (CartItem item : items) {
            Product product = products.get(item.getProduct().getId());
            product.setStock(product.getStock() - item.getQuantity());
        }
    }

    public synchronized void restoreStock(List<CartItem> items) {
        for (CartItem item : items) {
            Product product = products.get(item.getProduct().getId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
            }
        }
    }
}
