package com.melardev.spring.blogapi.services;

import com.melardev.spring.blogapi.enums.ArticleAuthorizationPolicy;
import com.melardev.spring.blogapi.enums.AuthorizationPolicy;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    public int getPageSize() {
        return 8;
    }

    public int getWhoCanEditArticles() {
        return 0;
    }

    public AuthorizationPolicy getWhoCanEditTweets() {
        return AuthorizationPolicy.ONLY_ADMIN;
    }

    public String getAdminRoleName() {
        return "ROLE_ADMIN";
    }

    public String getAnonymousRoleName() {
        return "ROLE_ANONYMOUS";
    }

    public ArticleAuthorizationPolicy getWhoCanDeleteArticles() {
        return ArticleAuthorizationPolicy.ONLY_ADMIN;
    }

    public ArticleAuthorizationPolicy getWhoCanCreateArticles() {
        return ArticleAuthorizationPolicy.ADMIN_AND_AUTHORS;
    }

    public String getDefaultAdminEmail() {
        return "admin@admin.com";
    }

    public String getDefaultAdminUsername() {
        return "admin";
    }

    public String getDefaultAdminPassword() {
        return "password";
    }

    public String getDefaultAdminLastName() {
        return "admin";
    }

    public String getDefaultAdminFirstName() {
        return "admin";
    }

    public AuthorizationPolicy getWhoCanCreateComments() {
        return AuthorizationPolicy.ADMIN_AND_OWNER;
    }

    public AuthorizationPolicy getWhoCanUpdateComments() {
        return AuthorizationPolicy.ADMIN_AND_OWNER;
    }

    public AuthorizationPolicy getWhoCanDeleteComments() {
        return AuthorizationPolicy.ONLY_ADMIN;
        //return config.getCommentPolicies().getDelete();
    }

    public String getCartKey() {
        return "CART";
    }

    public String getAuthenticatedRoleName() {
        return "ROLE_USER";
    }

    public int getMaxUsersToSeed() {
        return 60;
    }

    public int getMaxTagsToSeed() {
        return 7;
    }

    public AuthorizationPolicy getWhocanCheckout() {
        return AuthorizationPolicy.AUTHENTICATED_USER;
    }

    public int getLikesCountToSeed() {
        return 60;
    }

    public int getMaxReTweetsToSeed() {
        return 15;
    }

    public AuthorizationPolicy getWhoCanEditComments() {
        return AuthorizationPolicy.ONLY_ADMIN;
    }

    public int getMaxArticlesToSeed() {
        return 80;
    }

    public AuthorizationPolicy getWhoCanViewArticles() {
        return AuthorizationPolicy.ANY;
    }

    public int getMaxCommentsToSeed() {
        return 20;
    }

    public int getRepliesCountToSeed() {
        return 20;
    }

    public String getAuthorRoleName() {
        return "ROLE_AUTHOR";
    }

    public int getAuthorsCountToSeed() {
        return 12;
    }

    public ArticleAuthorizationPolicy getWhoCanUpdateArticles() {
        return ArticleAuthorizationPolicy.ADMIN_AND_AUTHORS;
    }

    public String getUploadsDirectory() {
        return System.getProperty("user.dir") + "/uploads";
    }
}