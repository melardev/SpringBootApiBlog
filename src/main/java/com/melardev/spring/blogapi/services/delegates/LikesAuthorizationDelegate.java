package com.melardev.spring.blogapi.services.delegates;

import com.melardev.spring.blogapi.entities.Like;
import com.melardev.spring.blogapi.entities.User;
import com.melardev.spring.blogapi.services.AuthorizationService;

public class LikesAuthorizationDelegate {
    private final AuthorizationService authorizationService;

    public LikesAuthorizationDelegate(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public boolean canLikeArticles(User user) {
        return user != null && user.getId() != null;
    }

    public boolean canDeleteLikes(Like like, User user) {
        return user != null && user.getId() != null && like != null && like.getUser().getId().equals(user.getId());
    }
}
