package com.melardev.spring.blogapi.dtos.response.subscriptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.melardev.spring.blogapi.dtos.response.users.partials.UserIdAndUsernameDto;
import com.melardev.spring.blogapi.entities.User;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionsListDto {
    @JsonProperty("following")
    private final List<UserIdAndUsernameDto> followingDtos;
    @JsonProperty("followers")
    private final List<UserIdAndUsernameDto> followerDtos;

    public SubscriptionsListDto(List<UserIdAndUsernameDto> followingDtos, List<UserIdAndUsernameDto> followerDtos) {
        this.followerDtos = followerDtos;
        this.followingDtos = followingDtos;
    }

    public static SubscriptionsListDto build(List<User> following, List<User> followers) {
        List<UserIdAndUsernameDto> followersDto = new ArrayList<>(followers.size());
        followers.forEach(follower -> {
            followersDto.add(UserIdAndUsernameDto.build(follower));
        });

        List<UserIdAndUsernameDto> followingDto = new ArrayList<>(following.size());
        following.forEach(followed -> {
            followingDto.add(UserIdAndUsernameDto.build(followed));
        });

        return new SubscriptionsListDto(followingDto, followersDto);
    }



    public List<UserIdAndUsernameDto> getFollowingDtos() {
        return followingDtos;
    }

    public List<UserIdAndUsernameDto> getFollowerDtos() {
        return followerDtos;
    }
}
