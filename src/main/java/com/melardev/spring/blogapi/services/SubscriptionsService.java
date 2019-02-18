package com.melardev.spring.blogapi.services;

import com.melardev.spring.blogapi.entities.User;
import com.melardev.spring.blogapi.entities.UserSubscription;
import com.melardev.spring.blogapi.entities.UserSubscriptionPK;
import com.melardev.spring.blogapi.errors.exceptions.ResourceNotFoundException;
import com.melardev.spring.blogapi.repository.UserSubscriptionsRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionsService {

    @Autowired
    UserSubscriptionsRepository userSubscriptionsRepository;

    @Autowired
    UsersService usersService;

    public ImmutablePair<List<User>, List<User>> findAll(User user, int page, int count) {
        return findAll(user, user, page, count);
    }

    public ImmutablePair<List<User>, List<User>> findAll(User user, User reference, int page, int count) {
        Page<UserSubscription> subscriptions = findAllRaw(user, page, count);
        List<User> following = new ArrayList<>();
        List<User> followers = new ArrayList<>();

        subscriptions.forEach(subscription -> {
            if (subscription.getFollower().getId().equals(reference.getId()))
                following.add(subscription.getFollowing());
            else
                followers.add(subscription.getFollower());
        });

        return new ImmutablePair<List<User>, List<User>>(following, followers);
    }

    public Page<UserSubscription> findAllRaw(User user, int page, int count) {
        //PageRequest pageRequest = PageRequest.of(page - 1, count, Sort.Direction.DESC, "createdAt");
        PageRequest pageRequest = PageRequest.of(page - 1, count);
        return userSubscriptionsRepository.findAllSubscriptions(user.getId(), pageRequest);
    }


    public Page<UserSubscription> findAllFollowers(User user, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        return userSubscriptionsRepository.findAllFollowers(user.getId(), pageRequest);
    }

    public Page<UserSubscription> findAllFollowing(User user, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        return userSubscriptionsRepository.findAllFollowing(user.getId(), pageRequest);
    }

    public UserSubscription create(User following, User follower) {
        UserSubscription subscription = new UserSubscription(following, follower);
        return userSubscriptionsRepository.save(subscription);
    }

    public UserSubscription create(String username, User follower) {
        return create(usersService.findByUsernameOrEmail(username), follower);
    }

    public UserSubscription find(Long followingId, Long followerId) {
        return find(followingId, followerId, true);
    }

    private UserSubscription find(Long followingId, Long followerId, boolean throwIfNotFound) {

        Optional<UserSubscription> subscription = userSubscriptionsRepository.findById(new UserSubscriptionPK(followerId, followingId));
        // Alternative approach. This should be true subscription.value == subscription2.value
        Optional<UserSubscription> subscription2 = userSubscriptionsRepository.findByIdFollowingIdAndIdFollowerId(followingId, followerId);
        if (subscription.isEmpty() && throwIfNotFound)
            throw new ResourceNotFoundException();

        return subscription.orElse(null);
    }

    public boolean delete(UserSubscription subscription) {
        userSubscriptionsRepository.delete(subscription);
        return true;
    }


}
