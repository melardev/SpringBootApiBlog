package com.melardev.spring.blogapi.repository;


import com.melardev.spring.blogapi.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    Role findByNameIgnoreCase(String name);
}
