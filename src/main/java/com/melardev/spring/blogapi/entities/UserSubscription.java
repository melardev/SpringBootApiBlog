package com.melardev.spring.blogapi.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_subscriptions", uniqueConstraints =
@UniqueConstraint(columnNames = {"follower_id", "following_id"}))
public class UserSubscription {

    @EmbeddedId
    UserSubscriptionPK id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("following_id")
    User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("follower_id")
    User following;

    public UserSubscription() {
    }

    public UserSubscription(User following, User follower) {
        this.following = following;
        this.follower = follower;
        this.id = new UserSubscriptionPK(follower.getId(), following.getId());
    }

    //Getters and setters omitted for brevity

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        UserSubscription that = (UserSubscription) o;
        return Objects.equals(follower, that.follower) &&
                Objects.equals(following, that.following);
    }

    @Override
    public int hashCode() {
        return Objects.hash(following, follower);
    }

    public UserSubscriptionPK getId() {
        return id;
    }

    public void setId(UserSubscriptionPK id) {
        this.id = id;
    }

    public User getFollowing() {
        return following;
    }

    public void setFollowing(User following) {
        this.following = following;
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

}
