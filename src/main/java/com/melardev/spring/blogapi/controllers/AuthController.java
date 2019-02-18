package com.melardev.spring.blogapi.controllers;

import com.melardev.spring.blogapi.config.JwtProvider;
import com.melardev.spring.blogapi.dtos.request.users.CreateUserDto;
import com.melardev.spring.blogapi.dtos.request.users.LoginDto;
import com.melardev.spring.blogapi.dtos.response.base.AppResponse;
import com.melardev.spring.blogapi.dtos.response.auth.JwtResponse;
import com.melardev.spring.blogapi.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    UsersController usersController;

    @PostMapping("register")
    public ResponseEntity<AppResponse> register(@Valid @RequestBody CreateUserDto dto, BindingResult bindingResult) {
        return usersController.registerUser(dto, bindingResult);
    }


    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("login")
    public ResponseEntity<AppResponse> login(@Valid @RequestBody LoginDto loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        User userPrinciple = (User) authentication.getPrincipal();

        User user = ((User) authentication.getPrincipal());
        return ResponseEntity.ok(JwtResponse.build(jwt, user));
    }
}
