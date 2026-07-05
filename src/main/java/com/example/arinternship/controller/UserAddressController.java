package com.example.arinternship.controller;

import com.example.arinternship.entity.User;
import com.example.arinternship.entity.UserAddress;
import com.example.arinternship.repository.UserAddressRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserAddressController {

    private final UserAddressRepository addressRepository;

    public UserAddressController(UserAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    private User getSessionUser(HttpSession session) {
        return (User) session.getAttribute("USER");
    }

    // ✅ GET — ดึงที่อยู่ทั้งหมดของ user
    @GetMapping
    public ResponseEntity<?> getMyAddresses(HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        List<Map<String, Object>> list = addressRepository
            .findByUserIdOrderByIsDefaultDescCreatedAtAsc(user.getId())
            .stream()
            .map(this::toMap)
            .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    // ✅ POST — เพิ่มที่อยู่ใหม่
    @PostMapping
    public ResponseEntity<?> addAddress(
            @RequestBody Map<String, Object> body,
            HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        String label         = (String) body.getOrDefault("label", "บ้าน");
        String recipientName = (String) body.get("recipientName");
        String phone         = (String) body.get("phone");
        String address       = (String) body.get("address");
        Boolean isDefault    = (Boolean) body.getOrDefault("isDefault", false);

        if (recipientName == null || phone == null || address == null) {
            return ResponseEntity.badRequest().body("กรุณากรอกข้อมูลให้ครบ");
        }

        // ถ้าตั้งเป็น default ให้ clear ที่อยู่อื่นก่อน
        if (Boolean.TRUE.equals(isDefault)) {
            addressRepository.clearDefaultByUserId(user.getId());
        }

        // ถ้ายังไม่มีที่อยู่เลย ให้เป็น default อัตโนมัติ
        long count = addressRepository
            .findByUserIdOrderByIsDefaultDescCreatedAtAsc(user.getId()).size();
        if (count == 0) isDefault = true;

        UserAddress addr = new UserAddress();
        addr.setUserId(user.getId());
        addr.setLabel(label);
        addr.setRecipientName(recipientName);
        addr.setPhone(phone);
        addr.setAddress(address);
        addr.setIsDefault(isDefault);

        addressRepository.save(addr);
        return ResponseEntity.ok(toMap(addr));
    }

    // ✅ PUT — แก้ไขที่อยู่
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        return addressRepository.findById(id).map(addr -> {
            // เช็คว่าเป็นเจ้าของ
            if (!addr.getUserId().equals(user.getId()))
                return ResponseEntity.status(403).<Object>body("ไม่มีสิทธิ์");

            if (body.containsKey("label"))         addr.setLabel((String) body.get("label"));
            if (body.containsKey("recipientName")) addr.setRecipientName((String) body.get("recipientName"));
            if (body.containsKey("phone"))         addr.setPhone((String) body.get("phone"));
            if (body.containsKey("address"))       addr.setAddress((String) body.get("address"));

            Boolean isDefault = (Boolean) body.get("isDefault");
            if (Boolean.TRUE.equals(isDefault)) {
                addressRepository.clearDefaultByUserId(user.getId());
                addr.setIsDefault(true);
            }

            addressRepository.save(addr);
            return ResponseEntity.ok(toMap(addr));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ DELETE — ลบที่อยู่
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(
            @PathVariable Long id,
            HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        return addressRepository.findById(id).map(addr -> {
            if (!addr.getUserId().equals(user.getId()))
                return ResponseEntity.status(403).<Object>body("ไม่มีสิทธิ์");

            boolean wasDefault = Boolean.TRUE.equals(addr.getIsDefault());
            addressRepository.delete(addr);

            // ถ้าลบที่อยู่ default ให้ตั้งอันแรกที่เหลือเป็น default แทน
            if (wasDefault) {
                List<UserAddress> remaining = addressRepository
                    .findByUserIdOrderByIsDefaultDescCreatedAtAsc(user.getId());
                if (!remaining.isEmpty()) {
                    remaining.get(0).setIsDefault(true);
                    addressRepository.save(remaining.get(0));
                }
            }
            return ResponseEntity.ok(Map.of("message", "ลบที่อยู่สำเร็จ"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ PATCH — ตั้งเป็น default
    @PatchMapping("/{id}/default")
    public ResponseEntity<?> setDefault(
            @PathVariable Long id,
            HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        return addressRepository.findById(id).map(addr -> {
            if (!addr.getUserId().equals(user.getId()))
                return ResponseEntity.status(403).<Object>body("ไม่มีสิทธิ์");

            addressRepository.clearDefaultByUserId(user.getId());
            addr.setIsDefault(true);
            addressRepository.save(addr);
            return ResponseEntity.ok(Map.of("message", "ตั้งเป็นที่อยู่หลักแล้ว"));
        }).orElse(ResponseEntity.notFound().build());
    }

    private Map<String, Object> toMap(UserAddress a) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",            a.getId());
        m.put("label",         a.getLabel());
        m.put("recipientName", a.getRecipientName());
        m.put("phone",         a.getPhone());
        m.put("address",       a.getAddress());
        m.put("isDefault",     Boolean.TRUE.equals(a.getIsDefault()));
        return m;
    }
}