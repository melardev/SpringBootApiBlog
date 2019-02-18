package com.melardev.spring.blogapi.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users_roles")
public class UserRole {

    @EmbeddedId
    private UserRolePK id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("role_id")
    Role role;

    public UserRolePK getId() {
        return id;
    }

    public void setId(UserRolePK id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        UserRole that = (UserRole) o;
        return Objects.equals(user, that.role) &&
                Objects.equals(role, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, role);
    }
}
