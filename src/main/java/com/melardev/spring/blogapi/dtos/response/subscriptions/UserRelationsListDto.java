package com.melardev.spring.blogapi.dtos.response.subscriptions;

import com.melardev.spring.blogapi.dtos.response.base.SuccessResponse;
import com.melardev.spring.blogapi.entities.UserSubscription;
import org.springframework.data.domain.Page;

public class UserRelationsListDto extends SuccessResponse {


    private final UserRelationsDataSection data;

    public UserRelationsListDto(UserRelationsDataSection data) {
        this.data = data;
    }

    public static UserRelationsListDto buildForFollowers(Page<UserSubscription> relations, String basePath) {
        return new UserRelationsListDto(UserRelationsDataSection.buildForFollowers(relations, basePath));
    }

    public static UserRelationsListDto buildForFollowing(Page<UserSubscription> relations, String basePath) {
        return new UserRelationsListDto(UserRelationsDataSection.buildForFollowing(relations, basePath));
    }

    public UserRelationsDataSection getData() {
        return data;
    }
}
