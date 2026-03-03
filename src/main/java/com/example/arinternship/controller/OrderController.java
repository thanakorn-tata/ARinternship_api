package com.example.arinternship.controller;

import com.example.arinternship.entity.*;
import com.example.arinternship.repository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class OrderController {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public OrderController(OrderRepository orderRepository,
                           CartRepository cartRepository,
                           ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    private User getSessionUser(HttpSession session) {
        return (User) session.getAttribute("USER");
    }

    private boolean isAdmin(HttpSession session) {
        User u = getSessionUser(session);
        return u != null && "ADMIN".equals(u.getRole());
    }

    private Map<String, Object> toOrderResponse(Order order) {
        List<Map<String, Object>> items = order.getItems() == null ? List.of() :
            order.getItems().stream().map(i -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", i.getId());
                m.put("productId", i.getProductId());
                m.put("productName", i.getProductName());
                m.put("productPrice", i.getProductPrice());
                m.put("quantity", i.getQuantity());
                m.put("subtotal", i.getSubtotal());
                return m;
            }).collect(Collectors.toList());

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("id", order.getId());
        res.put("status", order.getStatus());
        res.put("totalAmount", order.getTotalAmount());
        res.put("items", items);
        res.put("shippingName", order.getShippingName());
        res.put("shippingPhone", order.getShippingPhone());
        res.put("shippingAddress", order.getShippingAddress());
        res.put("createdAt", order.getCreatedAt());
        res.put("confirmedAt", order.getConfirmedAt());
        res.put("hasSlip", order.getSlipImage() != null);
        if (order.getUser() != null) {
            res.put("username", order.getUser().getUsername());
            res.put("userId", order.getUser().getId());
        }
        return res;
    }

    // ✅ เพิ่ม @Transactional เพราะมี deleteByUser
    @Transactional
    @PostMapping
    public ResponseEntity<?> createOrder(
        @RequestBody Map<String, String> body,
        HttpSession session
    ) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        List<Cart> cartItems = cartRepository.findByUser(user);
        if (cartItems.isEmpty()) return ResponseEntity.badRequest().body("ตะกร้าว่างเปล่า");

        Order order = new Order();
        order.setUser(user);
        order.setShippingName(body.getOrDefault("shippingName", ""));
        order.setShippingPhone(body.getOrDefault("shippingPhone", ""));
        order.setShippingAddress(body.getOrDefault("shippingAddress", ""));
        order.setStatus("PENDING_PAYMENT");

        List<OrderItem> orderItems = cartItems.stream().map(cart -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(cart.getProduct().getId());
            item.setProductName(cart.getProduct().getName());
            item.setProductPrice(cart.getProduct().getPrice());
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(cart.getProduct().getPrice() * cart.getQuantity());
            return item;
        }).collect(Collectors.toList());

        double total = orderItems.stream().mapToDouble(OrderItem::getSubtotal).sum();
        order.setTotalAmount(total);
        order.setItems(orderItems);

        Order saved = orderRepository.save(order);
        cartRepository.deleteByUser(user);  // ล้างตะกร้า

        return ResponseEntity.ok(toOrderResponse(saved));
    }

    // ✅ Upload Slip
    @Transactional
    @PostMapping(value = "/{id}/slip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadSlip(
        @PathVariable Long id,
        @RequestParam MultipartFile slip,
        HttpSession session
    ) throws IOException {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        return orderRepository.findById(id).map(order -> {
            if (!order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).<Object>body("ไม่มีสิทธิ์");
            }
            try {
                order.setSlipImage(slip.getBytes());
                order.setSlipImageType(slip.getContentType());
                order.setStatus("SLIP_UPLOADED");
                orderRepository.save(order);
                return ResponseEntity.ok(Map.of("message", "อัปโหลดสลิปสำเร็จ", "status", "SLIP_UPLOADED"));
            } catch (IOException e) {
                return ResponseEntity.status(500).<Object>body("อ่านไฟล์ไม่ได้");
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ ดู Slip image
    @GetMapping("/{id}/slip")
    public ResponseEntity<byte[]> getSlip(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).build();
        return orderRepository.findById(id).map(order -> {
            if (order.getSlipImage() == null) return ResponseEntity.notFound().<byte[]>build();
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, order.getSlipImageType() != null ? order.getSlipImageType() : "image/jpeg")
                .body(order.getSlipImage());
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Admin ยืนยัน Order
    @Transactional
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<?> confirmOrder(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("ไม่มีสิทธิ์");

        return orderRepository.findById(id).map(order -> {
            order.setStatus("CONFIRMED");
            order.setConfirmedAt(LocalDateTime.now());
            orderRepository.save(order);
            return ResponseEntity.ok(Map.of("message", "ยืนยัน order สำเร็จ", "status", "CONFIRMED"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Admin ดู Order ทั้งหมด
    @GetMapping("/admin")
    public ResponseEntity<?> getAllOrders(
        @RequestParam(required = false) String status,
        HttpSession session
    ) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("ไม่มีสิทธิ์");

        List<Order> orders = status != null && !status.isEmpty()
            ? orderRepository.findByStatusOrderByCreatedAtDesc(status)
            : orderRepository.findAllByOrderByCreatedAtDesc();

        return ResponseEntity.ok(orders.stream().map(this::toOrderResponse).collect(Collectors.toList()));
    }

    // ✅ User ดู Order ของตัวเอง
    @GetMapping("/my")
    public ResponseEntity<?> getMyOrders(HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
        return ResponseEntity.ok(orders.stream().map(this::toOrderResponse).collect(Collectors.toList()));
    }

    // ✅ ดู Order detail
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id, HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        return orderRepository.findById(id).map(order -> {
            boolean isOwner = order.getUser().getId().equals(user.getId());
            boolean admin = isAdmin(session);
            if (!isOwner && !admin) return ResponseEntity.status(403).<Object>body("ไม่มีสิทธิ์");
            return ResponseEntity.ok(toOrderResponse(order));
        }).orElse(ResponseEntity.notFound().build());
    }
}