// LoginResponse.java
package com.example.arinternship.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Boolean success;
    private String token;
    private String refreshToken;
    private UserDTO user;
    
    @Data
    @AllArgsConstructor
    public static class UserDTO {
        private Long id;
        private String username;
        private String fullname;
        private String email;
        private String role;
    }
}