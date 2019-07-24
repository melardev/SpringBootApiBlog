package com.melardev.spring.blogapi.controllers;

import com.melardev.spring.blogapi.dtos.request.users.CreateUserDto;
import com.melardev.spring.blogapi.dtos.request.users.LoginDto;
import com.melardev.spring.blogapi.dtos.response.base.AppResponse;
import com.melardev.spring.blogapi.dtos.response.base.ErrorResponse;
import com.melardev.spring.blogapi.dtos.response.base.SuccessResponse;
import com.melardev.spring.blogapi.entities.Role;
import com.melardev.spring.blogapi.entities.User;
import com.melardev.spring.blogapi.services.RolesService;
import com.melardev.spring.blogapi.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    UsersService usersService;

    @Autowired
    RolesService rolesService;

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    WebApplicationContext context;

    @PostMapping
    public ResponseEntity<AppResponse> registerUser(@Valid @RequestBody CreateUserDto createUserDto, BindingResult result) {
        if (usersService.existsByUsername(createUserDto.getUsername())) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("username", "Username already taken");
            return new ResponseEntity<AppResponse>(new ErrorResponse(errors),
                    HttpStatus.BAD_REQUEST);
        }

        if (usersService.existsByEmail(createUserDto.getEmail())) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("email", "Email already taken");
            return new ResponseEntity<AppResponse>(new ErrorResponse(errors), HttpStatus.BAD_REQUEST);
        }

        HashSet<Role> roles;

        roles = new HashSet<Role>(Collections.singletonList(rolesService.getOrCreate("ROLE_USER")));
        // Creating user's account
        User user = new User(createUserDto.getFirstName(), createUserDto.getLastName(), createUserDto.getEmail(),
                createUserDto.getUsername(), createUserDto.getPassword(), roles);

        usersService.createUser(user);

        return new ResponseEntity<AppResponse>(new SuccessResponse("User registered successfully"), HttpStatus.OK);

    }

    @PostMapping("login")
    public ResponseEntity<AppResponse> login(@Valid @RequestBody LoginDto loginRequest) {
        AuthController controller = context.getBean(AuthController.class);
        return controller.login(loginRequest);
    }

}