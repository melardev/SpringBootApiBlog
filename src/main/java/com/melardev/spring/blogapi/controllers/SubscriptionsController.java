package com.melardev.spring.blogapi.controllers;

import com.melardev.spring.blogapi.dtos.response.base.AppResponse;
import com.melardev.spring.blogapi.dtos.response.base.ErrorResponse;
import com.melardev.spring.blogapi.dtos.response.base.SuccessResponse;
import com.melardev.spring.blogapi.dtos.response.subscriptions.SubscriptionsListDto;
import com.melardev.spring.blogapi.dtos.response.subscriptions.UserRelationsListDto;
import com.melardev.spring.blogapi.entities.User;
import com.melardev.spring.blogapi.entities.UserSubscription;
import com.melardev.spring.blogapi.errors.exceptions.PermissionDeniedException;
import com.melardev.spring.blogapi.services.AuthorizationService;
import com.melardev.spring.blogapi.services.SubscriptionsService;
import com.melardev.spring.blogapi.services.UsersService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("subscriptions")
public class SubscriptionsController {

    private final UsersService usersService;
    private final SubscriptionsService subscriptionsService;
    private final AuthorizationService authorizationService;

    @Autowired
    public SubscriptionsController(UsersService usersService, SubscriptionsService subscriptionsService, AuthorizationService authorizationService) {
        this.usersService = usersService;
        this.subscriptionsService = subscriptionsService;
        this.authorizationService = authorizationService;
    }

    /**
     * This action is only used for educational purposes to show how bad is the direct ManyToMany query which
     * we trigger on getFollowers() and getFollowing(), notice how many queries are made to the Db
     * This is why the best approach always is to have complete control on the bridge table used in the Many to Many, see
     * index()
     *
     * @param page
     * @param request
     * @param pageSize
     * @return
     */
    @GetMapping("bad")
    public SubscriptionsListDto indexBad(@RequestParam(value = "page", defaultValue = "1") int page,
                                         HttpServletRequest request,
                                         @RequestParam(value = "page_size", defaultValue = "30") int pageSize) {
        List<User> followers = usersService.getCurrentLoggedInUser().getFollowers();
        List<User> following = usersService.getCurrentLoggedInUser().getFollowing();

        return SubscriptionsListDto.build(following, followers);
    }

    @GetMapping
    public SubscriptionsListDto index(@RequestParam(value = "page", defaultValue = "1") int page,
                                      HttpServletRequest request,
                                      @RequestParam(value = "page_size", defaultValue = "30") int pageSize) {
        ImmutablePair<List<User>, List<User>> userRelations = subscriptionsService.findAll(usersService.getCurrentLoggedInUser(), page, pageSize);

        return SubscriptionsListDto.build(userRelations.left, userRelations.right);
    }

    @GetMapping("followers")
    public UserRelationsListDto followers(@RequestParam(value = "page", defaultValue = "1") int page,
                                          HttpServletRequest request,
                                          @RequestParam(value = "page_size", defaultValue = "30") int pageSize) {
        Page<UserSubscription> followers = subscriptionsService.findAllFollowers(usersService.getCurrentLoggedInUser(), page, pageSize);

        return UserRelationsListDto.buildForFollowers(followers, request.getRequestURI().toString());
    }

    @GetMapping("following")
    public UserRelationsListDto following(@RequestParam(value = "page", defaultValue = "1") int page,
                                          HttpServletRequest request,
                                          @RequestParam(value = "page_size", defaultValue = "30") int pageSize) {

        Page<UserSubscription> following = subscriptionsService.findAllFollowing(usersService.getCurrentLoggedInUser(), page, pageSize);

        return UserRelationsListDto.buildForFollowing(following, request.getRequestURI().toString());
    }

    @PostMapping("{followingId}")
    public ResponseEntity<AppResponse> follow(@PathVariable("followingId") Long followingId) {
        User following = usersService.findById(followingId);
        User follower = usersService.getCurrentLoggedInUser();
        if (!this.canSubscribe(following, follower))
            throw new PermissionDeniedException("You are not allowed to subscribe to this user");
        UserSubscription subscription = subscriptionsService.create(following, follower);

        return new ResponseEntity<>(new SuccessResponse("Subscripbed to user " + subscription.getId().getFollowingId()), HttpStatus.OK);
    }

    @DeleteMapping("{followingId}")
    public AppResponse destroy(@PathVariable("followingId") Long followingId) {
        User follower = usersService.getCurrentLoggedInUser();
        if (follower == null)
            throw new PermissionDeniedException("You are not allowed to delete this subscription");

        UserSubscription subscription = subscriptionsService.find(followingId, follower.getId());
        if (!this.canUnsubscribe(subscription))
            throw new PermissionDeniedException("You are not allowed to delete this subscription");
        boolean success = subscriptionsService.delete(subscription);

        if (success)
            return new SuccessResponse("Ubsubscribed successfully");
        else
            return new ErrorResponse("Failed");
    }

    private boolean canSubscribe(User following, User follower) {
        return this.authorizationService.canSubscribe(following, follower);
    }

    private boolean canUnsubscribe(UserSubscription subscription) {
        return this.authorizationService.canUnsubscribe(subscription);
    }

}
