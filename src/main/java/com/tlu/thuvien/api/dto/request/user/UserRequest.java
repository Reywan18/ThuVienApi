package com.tlu.thuvien.api.dto.request.user;

import com.tlu.thuvien.domain.entity.UserRole;
import lombok.Data;

@Data
public class UserRequest {
    private Long id;
    private String name;
    private String email;
    private String password;
    private UserRole role;
}
