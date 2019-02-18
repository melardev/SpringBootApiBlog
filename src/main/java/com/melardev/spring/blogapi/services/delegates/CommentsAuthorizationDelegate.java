package com.melardev.spring.blogapi.services.delegates;

import com.melardev.spring.blogapi.entities.Comment;
import com.melardev.spring.blogapi.entities.User;
import com.melardev.spring.blogapi.enums.AuthorizationPolicy;
import com.melardev.spring.blogapi.services.AuthorizationService;

public class CommentsAuthorizationDelegate {
    private final AuthorizationService authorizationService;

    public CommentsAuthorizationDelegate(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public boolean canCreateComments() {
        AuthorizationPolicy whoCan = this.authorizationService.getSettingsService().getWhoCanCreateComments();
        switch (whoCan) {
            case AUTHENTICATED_USER:
            case ADMIN_AND_OWNER:
                return this.authorizationService.isLoggedIn();
            case ONLY_ADMIN:
                return this.authorizationService.isCurrentUserAdmin();
            case ANY:
            default:
                return true;
        }
    }

    private boolean ownsComment(Comment comment, User user) {
        // == or equals() ?
        return comment != null && comment.getUser() != null && comment.getUser() == user;
    }

    private boolean ownsComment(Comment comment) {
        return ownsComment(comment, this.authorizationService.getCurrentLoggedInUser());
    }


    public boolean canUpdateComments(Comment comment, User user) {
        AuthorizationPolicy crudPolicy = this.authorizationService.getSettingsService().getWhoCanEditComments();
        return can(crudPolicy, comment, user);
    }

    public boolean canDeleteComments(Comment comment, User user) {
        AuthorizationPolicy crudPolicy = this.authorizationService.getSettingsService().getWhoCanDeleteComments();
        return can(crudPolicy, comment, user);
    }

    private boolean can(AuthorizationPolicy crudPolicy, Comment comment, User user) {
        switch (crudPolicy) {
            case ONLY_ADMIN:
                return this.authorizationService.isCurrentUserAdmin();
            case ADMIN_AND_OWNER:
                return authorizationService.isCurrentUserAdmin() || this.ownsComment(comment, user);
            case AUTHENTICATED_USER:
                return this.authorizationService.isUserAuthenticated();
            default:
                return false;
        }
    }
}
