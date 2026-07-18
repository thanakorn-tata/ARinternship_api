package com.example.arinternship.controller;

import com.example.arinternship.entity.Product;
import com.example.arinternship.entity.User;
import com.example.arinternship.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("USER");
        return user != null && "ADMIN".equals(user.getRole());
    }

    // ✅ GET ALL — admin เห็นทั้งหมด, user/guest เห็นเฉพาะ active
    @GetMapping
    public List<Product> getAll(HttpSession session) {
        List<Product> products = isAdmin(session)
            ? repository.findAll()
            : repository.findByActiveTrue();
        products.forEach(p -> p.setImageData(null));
        return products;
    }

    // ✅ GET ACTIVE ONLY — สำหรับหน้าร้านโดยเฉพาะ ไม่ต้องพึ่ง session
    @GetMapping("/active")
    public List<Product> getActiveOnly() {
        List<Product> products = repository.findByActiveTrue();
        products.forEach(p -> p.setImageData(null));
        return products;
    }

    // ✅ GET IMAGE
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        return repository.findById(id).map(product -> {
            if (product.getImageData() == null) {
                return ResponseEntity.notFound().<byte[]>build();
            }
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                    product.getImageType() != null ? product.getImageType() : "image/jpeg")
                .body(product.getImageData());
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ CREATE
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
        @RequestParam String name,
        @RequestParam(required = false) String description,
        @RequestParam Double price,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Integer stock,
        @RequestParam(required = false) MultipartFile image,
        HttpSession session
    ) throws IOException {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("ไม่มีสิทธิ์");

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setStock(stock != null ? stock : 0);
        product.setActive(true);

        if (image != null && !image.isEmpty()) {
            product.setImageData(image.getBytes());
            product.setImageType(image.getContentType());
        }

        Product saved = repository.save(product);
        saved.setImageData(null);
        return ResponseEntity.ok(saved);
    }

    // ✅ UPDATE
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
        @PathVariable Long id,
        @RequestParam String name,
        @RequestParam(required = false) String description,
        @RequestParam Double price,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Integer stock,
        @RequestParam(required = false) MultipartFile image,
        HttpSession session
    ) throws IOException {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("ไม่มีสิทธิ์");

        return repository.findById(id).map(product -> {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            product.setStock(stock != null ? stock : 0);

            if (image != null && !image.isEmpty()) {
                try {
                    product.setImageData(image.getBytes());
                    product.setImageType(image.getContentType());
                } catch (IOException e) {
                    throw new RuntimeException("อ่านไฟล์รูปไม่ได้");
                }
            }

            Product saved = repository.save(product);
            saved.setImageData(null);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ TOGGLE ACTIVE/INACTIVE
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<?> toggleActive(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("ไม่มีสิทธิ์");

        return repository.findById(id).map(product -> {
            product.setActive(!product.getActive());
            repository.save(product);
            return ResponseEntity.ok(Map.of(
                "id", product.getId(),
                "active", product.getActive(),
                "message", product.getActive() ? "เปิดขายแล้ว" : "ปิดขายแล้ว"
            ));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("ไม่มีสิทธิ์");
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}