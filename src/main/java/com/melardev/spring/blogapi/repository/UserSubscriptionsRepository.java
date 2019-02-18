package com.melardev.spring.blogapi.repository;

import com.melardev.spring.blogapi.entities.UserSubscription;
import com.melardev.spring.blogapi.entities.UserSubscriptionPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserSubscriptionsRepository extends JpaRepository<UserSubscription, UserSubscriptionPK> {

    // ===
    // === Get subscriptions related to a user
    // ===
    @Query("from UserSubscription ur where ur.follower.id=:id or ur.following.id=:id")
    Page<UserSubscription> findAllSubscriptions(@Param("id") Long id, Pageable pageRequest);

    /**
     * Retrieves all subscriptions from the given user
     * Same as findAllSubscriptions(followingId, followerId) or
     * findAllSubscriptions(userId)
     *
     * @param followingId
     * @param followerId
     * @return
     */
    UserSubscription findByIdFollowingIdOrIdFollowerId(Long followingId, Long followerId);

    default UserSubscription findByIdFollowingIdOrIdFollowerId(Long userId) {
        return findByIdFollowingIdOrIdFollowerId(userId, userId);
    }

    // ===
    // === Get following/subscriptions
    // ===
    public List<UserSubscription> findByIdFollowerId(Long followerId);

    public Page<UserSubscription> findByIdFollowerId(Long followerId, Pageable pageable);

    @Query("from UserSubscription ur where ur.follower.id=:followerId")
    public List<UserSubscription> findAllFollowing(Long followerId);

    @Query("select ur.following.id from UserSubscription ur where ur.follower.id=:followerId")
    public List<Object> findAllFollowingIds(Long followerId);

    @Query("from UserSubscription ur where ur.follower.id=:followerId")
    public Page<UserSubscription> findAllFollowing(Long followerId, Pageable pageable);

    // ===
    // === Get followers
    // ===
    public List<UserSubscription> findByIdFollowingId(Long followingId);

    public Page<UserSubscription> findByIdFollowingId(Long followingId, Pageable pageable);

    @Query("from UserSubscription ur where ur.following.id=:followingId")
    public Page<UserSubscription> findAllFollowers(Long followingId, Pageable pageable);

    @Query("from UserSubscription ur where ur.following.id=:followingId")
    public List<UserSubscription> findAllFollowers(Long followingId);

    // ===
    // === Get specific subscription
    // ===
    Optional<UserSubscription> findByIdFollowingIdAndIdFollowerId(Long followingId, Long followerId);

    @Query("from UserSubscription ur where ur.following.id=:followingId and ur.follower.id=:followerId")
    Optional<UserSubscription> findSubscription(Long followingId, Long followerId);


    @Query("select us.following.id from UserSubscription us where us.follower.id = :userId")
    List<Long> fetchAllIdsSubscribedTo(@Param("userId") Long userId);
}
