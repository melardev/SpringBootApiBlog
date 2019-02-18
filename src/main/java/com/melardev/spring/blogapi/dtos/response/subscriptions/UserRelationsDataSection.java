package com.melardev.spring.blogapi.dtos.response.subscriptions;

import com.melardev.spring.blogapi.dtos.response.shared.PageMeta;
import com.melardev.spring.blogapi.dtos.response.users.partials.UserIdAndUsernameDto;
import com.melardev.spring.blogapi.entities.UserSubscription;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class UserRelationsDataSection {
    private final List<UserIdAndUsernameDto> relations;
    private final PageMeta pageMeta;

    public UserRelationsDataSection(PageMeta pageMeta, List<UserIdAndUsernameDto> relationDtos) {
        this.pageMeta = pageMeta;
        this.relations = relationDtos;
    }

    public static UserRelationsDataSection buildForFollowers(Page<UserSubscription> relations, String basePath) {
        List<UserIdAndUsernameDto> relationDtos = getFollowers(relations);
        return new UserRelationsDataSection(PageMeta.build(relations, basePath), relationDtos);
    }

    public static UserRelationsDataSection buildForFollowing(Page<UserSubscription> relations, String basePath) {
        List<UserIdAndUsernameDto> relationDtos = getFollowing(relations);
        return new UserRelationsDataSection(PageMeta.build(relations, basePath), relationDtos);
    }

    private static List<UserIdAndUsernameDto> getFollowing(Page<UserSubscription> relations) {
        List<UserIdAndUsernameDto> relationDtos = new ArrayList<>();

        relations.forEach(relation -> relationDtos.add(UserIdAndUsernameDto.build(relation.getFollowing())));
        return relationDtos;
    }

    private static List<UserIdAndUsernameDto> getFollowers(Page<UserSubscription> relations) {
        List<UserIdAndUsernameDto> relationDtos = new ArrayList<>();

        relations.forEach(relation -> relationDtos.add(UserIdAndUsernameDto.build(relation.getFollower())));
        return relationDtos;
    }

    public List<UserIdAndUsernameDto> getRelations() {
        return relations;
    }

    public PageMeta getPageMeta() {
        return pageMeta;
    }
}
