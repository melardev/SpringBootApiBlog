package com.melardev.spring.blogapi.entities.extensions;

import com.melardev.spring.blogapi.entities.User;
import com.melardev.spring.blogapi.errors.exceptions.UnexpectedStateException;

public class UserExtension extends User {

    private boolean isFollower;
    private boolean isFollowing;

    public UserExtension(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public UserExtension(Integer id, String followerUsername, String followingUserName, Long followerId, Long followingId) {

        // If the source user Id is the follower Id that means we have retrieved the following
        if (id.longValue() == followerId) {
            this.id = followingId;
            this.isFollower = false;
            this.isFollowing = true;
            setUsername(followingUserName);
        } else if (id.longValue() == followingId) {
            // If the source user Id is the follower Id that means we have retrieved a follower
            this.id = followerId;
            this.isFollower = true;
            this.isFollowing = false;
            setUsername(followerUsername);
        } else
            throw new UnexpectedStateException();
    }

    public boolean isFollower() {
        return isFollower;
    }

    public void setFollower(boolean follower) {
        isFollower = follower;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }
}
