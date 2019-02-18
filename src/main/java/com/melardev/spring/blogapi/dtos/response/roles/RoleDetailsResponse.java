package com.melardev.spring.blogapi.dtos.response.roles;

import com.melardev.spring.blogapi.entities.Role;

public class RoleDetailsResponse {
    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
