package com.example.arinternship.controller;

import com.example.arinternship.dto.CartItemResponse;
import com.example.arinternship.entity.Cart;
import com.example.arinternship.entity.Product;
import com.example.arinternship.entity.User;
import com.example.arinternship.repository.CartRepository;
import com.example.arinternship.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartController(CartRepository cartRepository,
                          ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    // ✅ Helper ดึง user จาก session
    private User getSessionUser(HttpSession session) {
        return (User) session.getAttribute("USER");
    }

    // ✅ GET — ดึงตะกร้าของ user
    @GetMapping
    public ResponseEntity<?> getCart(HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        List<CartItemResponse> items = cartRepository.findByUser(user)
            .stream()
            .map(cart -> new CartItemResponse(
                cart.getId(),
                cart.getProduct().getId(),
                cart.getProduct().getName(),
                cart.getProduct().getPrice(),
                cart.getQuantity()
            ))
            .collect(Collectors.toList());

        double total = items.stream().mapToDouble(CartItemResponse::getSubtotal).sum();

        return ResponseEntity.ok(Map.of(
            "items", items,
            "total", total,
            "count", items.stream().mapToInt(CartItemResponse::getQuantity).sum()
        ));
    }

    // ✅ POST — เพิ่มสินค้าลงตะกร้า
    @PostMapping
    public ResponseEntity<?> addToCart(
        @RequestBody Map<String, Object> body,
        HttpSession session
    ) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        Long productId = Long.valueOf(body.get("productId").toString());
        int quantity = body.containsKey("quantity")
            ? Integer.parseInt(body.get("quantity").toString()) : 1;

        Product product = productRepository.findById(productId)
            .orElse(null);
        if (product == null) return ResponseEntity.badRequest().body("ไม่พบสินค้า");

        // ✅ ถ้ามีอยู่แล้ว → เพิ่มจำนวน
        Cart cart = cartRepository.findByUserAndProductId(user, productId)
            .orElse(null);

        if (cart != null) {
            cart.setQuantity(cart.getQuantity() + quantity);
        } else {
            cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(quantity);
        }

        cartRepository.save(cart);
        return ResponseEntity.ok(Map.of("message", "เพิ่มลงตะกร้าแล้ว"));
    }

    // ✅ PUT — อัปเดตจำนวน
    @PutMapping("/{cartId}")
    public ResponseEntity<?> updateQuantity(
        @PathVariable Long cartId,
        @RequestBody Map<String, Object> body,
        HttpSession session
    ) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        int quantity = Integer.parseInt(body.get("quantity").toString());

        return cartRepository.findById(cartId).map(cart -> {
            // ✅ เช็คว่าเป็นของ user คนนี้จริงๆ
            if (!cart.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).<Object>body("ไม่มีสิทธิ์");
            }
            if (quantity <= 0) {
                cartRepository.delete(cart);
                return ResponseEntity.ok(Map.of("message", "ลบออกจากตะกร้าแล้ว"));
            }
            cart.setQuantity(quantity);
            cartRepository.save(cart);
            return ResponseEntity.ok(Map.of("message", "อัปเดตจำนวนแล้ว"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ DELETE — ลบสินค้าออกจากตะกร้า
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> removeFromCart(
        @PathVariable Long cartId,
        HttpSession session
    ) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        return cartRepository.findById(cartId).map(cart -> {
            if (!cart.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).<Object>body("ไม่มีสิทธิ์");
            }
            cartRepository.delete(cart);
            return ResponseEntity.ok(Map.of("message", "ลบออกจากตะกร้าแล้ว"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ DELETE ALL — ล้างตะกร้าทั้งหมด
    @DeleteMapping
    public ResponseEntity<?> clearCart(HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        cartRepository.deleteByUser(user);
        return ResponseEntity.ok(Map.of("message", "ล้างตะกร้าแล้ว"));
    }
}