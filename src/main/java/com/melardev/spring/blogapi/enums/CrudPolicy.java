package com.melardev.spring.blogapi.enums;

enum CrudPolicy{
    ONLY_ADMIN, ADMIN_AND_OWNER, AUTHENTICATED_USER, ANY
}