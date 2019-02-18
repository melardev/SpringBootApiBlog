package com.melardev.spring.blogapi.dtos.response.likes.partials;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.melardev.spring.blogapi.dtos.response.articles.partials.ArticleElementalDto;
import com.melardev.spring.blogapi.dtos.response.users.partials.UserIdAndUsernameDto;
import com.melardev.spring.blogapi.entities.Like;

public class AdminLikeSummary extends LikeSummaryDto {

    @JsonProperty("user")
    private final UserIdAndUsernameDto userDto;

    private AdminLikeSummary(ArticleElementalDto articleDto, UserIdAndUsernameDto userDto) {
        super(articleDto);
        this.userDto = userDto;
    }

    public static AdminLikeSummary build(Like like) {
        return new AdminLikeSummary(ArticleElementalDto.build(like.getArticle()), UserIdAndUsernameDto.build(like.getUser()));
    }

    public UserIdAndUsernameDto getUserDto() {
        return userDto;
    }
}
