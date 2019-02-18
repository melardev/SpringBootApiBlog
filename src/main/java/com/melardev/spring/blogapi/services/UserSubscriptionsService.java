package com.melardev.spring.blogapi.services;

import com.melardev.spring.blogapi.entities.User;
import com.melardev.spring.blogapi.entities.UserSubscription;
import com.melardev.spring.blogapi.entities.extensions.UserExtension;
import com.melardev.spring.blogapi.errors.exceptions.UnexpectedStateException;
import com.melardev.spring.blogapi.repository.UserSubscriptionsRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserSubscriptionsService {

    @Autowired
    UserSubscriptionsRepository userRelationsRepository;
    @PersistenceContext
    EntityManager entityManager;

    public Page<UserSubscription> getFollowersOf() {
        return null;
    }

    public Pair<List<User>, List<User>> getFromUser(User user) {
        List<UserExtension> relations = getRelationsFromUser(user.getId());
        List<User> followers = new ArrayList<>();
        List<User> following = new ArrayList<>();

        for (UserExtension userExtension : relations) {
            if (userExtension.isFollower())
                followers.add(userExtension);
            else if (userExtension.isFollowing())
                following.add(userExtension);
            else
                throw new UnexpectedStateException();
        }

        return Pair.of(followers, following);
    }


    private List<UserExtension> getRelationsFromUser(Long id) {
        String hql = "select new com.melardev.spring.blogapi.entities.extensions.UserExtension(" + id + ", ur.follower.username, ur.following.username, ur.follower.id, ur.following.id) from UserRelations ur where ur.follower.id=:userId or ur.following.id=:userId";
        Session session = (Session) entityManager.getDelegate();
        return session.createQuery(hql)
                .setParameter("userId", id)
                .list();
    }

    public long getAllCount() {
        return userRelationsRepository.count();
    }

    public List<Long> fetchAllIdsSubscribedTo(Long userId) {
        return userRelationsRepository.fetchAllIdsSubscribedTo(userId);
    }
}
