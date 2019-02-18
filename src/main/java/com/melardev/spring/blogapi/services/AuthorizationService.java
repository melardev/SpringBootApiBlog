package com.melardev.spring.blogapi.services;

import com.melardev.spring.blogapi.entities.*;
import com.melardev.spring.blogapi.enums.AuthorizationPolicy;
import com.melardev.spring.blogapi.enums.CrudOperation;
import com.melardev.spring.blogapi.services.delegates.ArticlesAuthorizationDelegate;
import com.melardev.spring.blogapi.services.delegates.CommentsAuthorizationDelegate;
import com.melardev.spring.blogapi.services.delegates.LikesAuthorizationDelegate;
import com.melardev.spring.blogapi.services.delegates.SubscriptionsDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.melardev.spring.blogapi.enums.AuthorizationPolicy.*;

@Service
public class AuthorizationService {

    // @Value("app.security.authorization.policies.update")
    private int whoCanEdit;

    SettingsService settingsService;
    private UsersService usersService;
    private CommentsAuthorizationDelegate commentsAuthorizationDelegate;
    private ArticlesAuthorizationDelegate articlesAuthorizationDelegate;
    private LikesAuthorizationDelegate likesAuthorizationDelegate;
    private SubscriptionsDelegate subscriptionDelegate;

    @Autowired
    public AuthorizationService(SettingsService settingsService, UsersService usersService) {
        this.settingsService = settingsService;
        this.usersService = usersService;
    }

    @PostConstruct
    public void init() {
        this.commentsAuthorizationDelegate = new CommentsAuthorizationDelegate(this);
        this.articlesAuthorizationDelegate = new ArticlesAuthorizationDelegate(this);
        this.likesAuthorizationDelegate = new LikesAuthorizationDelegate(this);
        this.subscriptionDelegate = new SubscriptionsDelegate(this);
    }

    public boolean canEdit(Article article, User user) {
        if (whoCanEdit == 0) {
            // admin
            return hasAdminRole(user);
        } else
            return hasAdminRole(user) || ownsArticle(article, user);
    }

    private boolean hasAdminRole(User user) {
        return false;
    }

    private boolean ownsArticle(Article article, User user) {
        // == or equals() ?
        return article != null && article.getUser() != null && article.getUser() == user;
    }

    private boolean ownsArticle(Article article) {
        return ownsArticle(article, this.usersService.getCurrentLoggedInUser());
    }


    //@Override
    public boolean isAnonymous(final Authentication authentication) {
        Collection<? extends GrantedAuthority> auths = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for (GrantedAuthority a : auths) {
            roles.add(a.getAuthority());
        }
        if (roles.contains("ROLE_ANONYMOUS") || roles.size() == 0) {
            System.out.println("anon");
            return true;
        } else {
            System.out.println("not anon");
            return false;
        }
    }


    public boolean canViewArticle(Article article) {
        AuthorizationPolicy crudPolicy = this.settingsService.getWhoCanViewArticles();
        if (crudPolicy == ONLY_ADMIN)
            return this.isCurrentUserAdmin();
        else if (crudPolicy == ADMIN_AND_OWNER)
            return this.isCurrentUserAdmin() || this.ownsArticle(article);
        else if (crudPolicy == AUTHENTICATED_USER)
            return this.isUserAuthenticated();
        else
            return false;
    }

    public boolean isUserAuthenticated() {
        return this.usersService.isLoggedIn();
    }

    public boolean can(IUserOwnedResource resource, CrudOperation operation) {
        return false;
        /* if(operation == CrudOperation.CREATE){
            String resourceName = resource.getResourceName();
            if(resourceName.equalsIgnoreCase("comments")){ // typeof Comment
                CrudPolicy crudPolicy = this.settingsService.getCommentsCreatePolicy();
                switch(crudPolicy){
                    case CrudPolicy.ONLY_ADMIN:
                    return this.usersService.isAdmin();
                    case CrudPolicy.ADMIN_AND_OWNER:
                    case CrudPolicy.AUTHENTICATED_USER:
                    return this.usesService.isAuthenticated();
                    default:
                    return false;
                }
            }else if(resource instanceof Article){
                CrudPolicy crudPolicy = this.settingsService.getArticlesCreatePolicy();
                switch(crudPolicy){
                    case CrudPolicy.ONLY_ADMIN:
                    return this.usersService.isAdmin();
                    case CrudPolicy.ADMIN_AND_OWNER:
                    case CrudPolicy.AUTHENTICATED_USER:
                    return this.usesService.isAuthenticated();
                    default:
                    return false;
                }
            }else{
                throw new NotExpectedConditionException();
            }
        }
        */
    }

    public boolean canCreateComments() {
        return commentsAuthorizationDelegate.canCreateComments();
    }

    public boolean canUpdateComments(Comment comment, User user) {
        return commentsAuthorizationDelegate.canUpdateComments(comment, user);
    }


    public boolean canDeleteComments(Comment comment) {
        return canDeleteComments(comment, this.usersService.getCurrentLoggedInUser());
    }

    public boolean canDeleteComments(Comment comment, User user) {
        return commentsAuthorizationDelegate.canDeleteComments(comment, user);
    }

    public boolean canLikeArticles() {
        return canLikeArticles(this.usersService.getCurrentLoggedInUser());
    }

    public boolean canLikeArticles(User user) {
        return likesAuthorizationDelegate.canLikeArticles(user);
    }

    public boolean canDeleteLikes(Like like, User user) {
        return likesAuthorizationDelegate.canDeleteLikes(like, user);
    }

    public SettingsService getSettingsService() {
        return settingsService;
    }


    public boolean canCreateArticles(User user) {
        return articlesAuthorizationDelegate.canCreateArticles(user);
    }


    public boolean canUpdateArticles(Article article, User user) {
        return articlesAuthorizationDelegate.canUpdateArticles(article, user);
    }

    public boolean canDeleteArticles(Article article, User user) {
        return articlesAuthorizationDelegate.canDeleteArticles(article, user);
    }

    public boolean canSubscribe(User following, User follower) {
        return subscriptionDelegate.canSubscribe(following, follower);
    }

    public boolean canSubscribe(User following) {
        return subscriptionDelegate.canSubscribe(following);
    }

    public boolean canUnsubscribe(UserSubscription subscription) {
        return subscriptionDelegate.canUnSubscribe(subscription, getCurrentLoggedInUser());
    }

    public User getCurrentLoggedInUser() {
        return usersService.getCurrentLoggedInUser();
    }

    public boolean isCurrentUserAuthor() {
        return usersService.isAuthor();
    }

    public boolean isLoggedIn() {
        return usersService.isLoggedIn();
    }

    public boolean isCurrentUserAdmin() {
        return isCurrentUserInRole(this.settingsService.getAdminRoleName());
    }

    private boolean isCurrentUserInRole(String authority) {
        Authentication authentication = this.usersService.getCurrentLoggedInAuthenticationObject();
        if (authentication == null) return false;
        return isUserInRole(authentication, authority);
    }

    public boolean isUserInRole(Authentication authentication, String authority) {
        if (authentication == null)
            return false;
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }

    public boolean isUserInRole(User user, String authority) {
        if (user == null)
            return false;
        return user.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }

    public boolean isAuthor(User user) {
        return isUserInRole(user, settingsService.getAuthorRoleName());
    }

    public boolean isUserAuthenticated(User follower) {
        return follower != null && getCurrentLoggedInUser().getId().equals(follower.getId());
    }

    public boolean isAdmin(User following) {
        return isUserInRole(following, settingsService.getAdminRoleName());
    }
}