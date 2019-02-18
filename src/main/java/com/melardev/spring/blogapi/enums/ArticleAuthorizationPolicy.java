package com.melardev.spring.blogapi.enums;

public enum ArticleAuthorizationPolicy {
    ONLY_ADMIN, ONLY_AUTHORS, ADMIN_AND_AUTHORS, ADMIN_AND_OWNER, ANY_AUTHENTICATED, ANY
}
