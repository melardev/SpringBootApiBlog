package com.melardev.spring.blogapi.filters;

import com.melardev.spring.blogapi.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminAuthorizationFilter extends OncePerRequestFilter {

    private UsersService usersService;

    @Autowired
    public AdminAuthorizationFilter(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Check url /admin, check roles, throw exception if fail

        filterChain.doFilter(request, response);
    }

}