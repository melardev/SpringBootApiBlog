package com.melardev.spring.blogapi.services;

import com.melardev.spring.blogapi.entities.Role;
import com.melardev.spring.blogapi.errors.exceptions.ResourceNotFoundException;
import com.melardev.spring.blogapi.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class RolesService {

    private final RolesRepository rolesRepository;

    @Autowired
    public RolesService(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    public Role save(Role role) {
        return this.rolesRepository.save(role);
    }

    public Role save(String name) {
        return this.rolesRepository.save(new Role(name));
    }

    public Role get(String name, boolean throwExceptionIfNotFound) {
        Optional<Role> role = this.rolesRepository.findByName(name);
        if (throwExceptionIfNotFound && !role.isPresent())
            throw new ResourceNotFoundException();
        return role.orElse(null);
    }

    public Role getRoleDontThrow(String name) {
        return get(name, false);
    }

    public Role getRoleOrThrow(String name) {
        return get(name, true);
    }

    public List<Role> findAll() {
        return rolesRepository.findAll();
    }

    public Role getOrCreate(String roleName) {
        Optional<Role> role = rolesRepository.findByName(roleName);
        return role.orElseGet(new Supplier<Role>() {
            @Override
            public Role get() {
                return rolesRepository.save(new Role(roleName));
            }
        });
    }

    public Page<Role> findAll(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt");
        return this.rolesRepository.findAll(pageRequest);
    }

    public Role findById(Long id) {
        return this.rolesRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public Role update(Role role) {
        return rolesRepository.save(role);
    }

    public Role getReference(Long id) {
        return this.rolesRepository.getOne(id);
    }

    public Role getOrCreate(String name, String description) {
        Role c = rolesRepository.findByNameIgnoreCase(name);
        if (c == null)
            c = rolesRepository.save(new Role(name, description));

        return c;
    }

    public Role findOrCreateByName(String name) {
        return findOrCreateByName(name, null);
    }

    public Role findOrCreateByName(String name, String description) {
        Role tag = rolesRepository.findByName(name).orElse(null);
        if (tag == null) {
            tag = rolesRepository.save(new Role(name, description));
        }
        return tag;
    }
}

