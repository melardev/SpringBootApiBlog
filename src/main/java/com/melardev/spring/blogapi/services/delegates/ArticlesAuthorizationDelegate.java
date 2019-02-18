package com.melardev.spring.blogapi.services.delegates;

import com.melardev.spring.blogapi.entities.Article;
import com.melardev.spring.blogapi.entities.User;
import com.melardev.spring.blogapi.enums.ArticleAuthorizationPolicy;
import com.melardev.spring.blogapi.services.AuthorizationService;

public class ArticlesAuthorizationDelegate {
    private final AuthorizationService authorizationService;

    public ArticlesAuthorizationDelegate(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public boolean canCreateArticles(User user) {
        ArticleAuthorizationPolicy whoCan = authorizationService.getSettingsService().getWhoCanCreateArticles();
        return can(whoCan, null, user);
    }

    public boolean canUpdateArticles(Article article, User user) {
        ArticleAuthorizationPolicy crudPolicy = this.authorizationService.getSettingsService().getWhoCanUpdateArticles();
        return can(crudPolicy, article, user);
    }

    private boolean can(ArticleAuthorizationPolicy crudPolicy, Article article, User user) {
        switch (crudPolicy) {
            case ADMIN_AND_AUTHORS:
                return authorizationService.isCurrentUserAdmin() || authorizationService.isCurrentUserAuthor();
            case ONLY_ADMIN:
                return authorizationService.isLoggedIn();
            case ADMIN_AND_OWNER:
                return authorizationService.isCurrentUserAdmin() || ownsArticle(article, user);
            case ANY:
                return true;
            default:
                return false;
        }
    }


    private boolean ownsArticle(Article article, User user) {
        return article.getUser() == user;
    }


    public boolean canDeleteArticles(Article article, User user) {
        ArticleAuthorizationPolicy whoCan = authorizationService.getSettingsService().getWhoCanDeleteArticles();
        return can(whoCan, article, user);
    }
}

