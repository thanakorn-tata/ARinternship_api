package com.example.arinternship.dto;

// ✅ DTO สำหรับ response ตะกร้า ไม่ส่ง entity ตรงๆ
public class CartItemResponse {

    private Long cartId;
    private Long productId;
    private String productName;
    private Double productPrice;
    private String imageUrl;
    private Integer quantity;
    private Double subtotal;

    public CartItemResponse(Long cartId, Long productId, String productName,
                            Double productPrice, Integer quantity) {
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.subtotal = productPrice * quantity;
        this.imageUrl = "http://localhost:8080/api/products/" + productId + "/image";
    }

    public Long getCartId()          { return cartId; }
    public Long getProductId()       { return productId; }
    public String getProductName()   { return productName; }
    public Double getProductPrice()  { return productPrice; }
    public String getImageUrl()      { return imageUrl; }
    public Integer getQuantity()     { return quantity; }
    public Double getSubtotal()      { return subtotal; }
}