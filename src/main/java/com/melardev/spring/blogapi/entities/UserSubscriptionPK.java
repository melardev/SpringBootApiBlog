package com.melardev.spring.blogapi.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserSubscriptionPK implements Serializable {
    @Column(name = "follower_id")
    private Long followerId;

    @Column(name = "following_id")
    private Long followingId;

    private UserSubscriptionPK() {
    }

    public UserSubscriptionPK(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }

    //Getters omitted for brevity

    public Long getFollowerId() {
        return followerId;
    }

    public Long getFollowingId() {
        return followingId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        UserSubscriptionPK that = (UserSubscriptionPK) o;
        return Objects.equals(followerId, that.followerId) &&
                Objects.equals(followingId, that.followingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followingId);
    }
}
