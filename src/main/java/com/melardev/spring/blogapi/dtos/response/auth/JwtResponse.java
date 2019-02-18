package com.melardev.spring.blogapi.dtos.response.auth;

import com.melardev.spring.blogapi.dtos.response.base.SuccessResponse;
import com.melardev.spring.blogapi.dtos.response.auth.partials.UserJwtResponse;
import com.melardev.spring.blogapi.entities.User;

public class JwtResponse extends SuccessResponse {
    private final UserJwtResponse user;
    private String tokenScheme = "Bearer";
    private String token;

    private JwtResponse(String jwt, UserJwtResponse user) {
        this.token = jwt;
        this.user = user;
    }


    public static JwtResponse build(String jwt, User user) {
        return new JwtResponse(jwt, UserJwtResponse.build(user.getUsername(), user.getEmail(),
                user.getAuthorities(), jwt));
    }


    public UserJwtResponse getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenScheme() {
        return tokenScheme;
    }

    public void setTokenScheme(String tokenScheme) {
        this.tokenScheme = tokenScheme;
    }

}