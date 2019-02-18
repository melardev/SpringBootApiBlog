package com.melardev.spring.blogapi.dtos.response.articles.partials;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.melardev.spring.blogapi.dtos.response.users.partials.UserIdAndUsernameDto;
import com.melardev.spring.blogapi.entities.Article;

public class ArticleElementalDto {
    private final Long id;
    private final String title;
    private final String slug;
    @JsonProperty("user")
    private final UserIdAndUsernameDto userDto;

    private ArticleElementalDto(Long id, String title, String slug, UserIdAndUsernameDto userDto) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.userDto = userDto;
    }

    public static ArticleElementalDto build(Article article) {
        return new ArticleElementalDto(
                article.getId(), article.getTitle(), article.getSlug(),
                UserIdAndUsernameDto.build(article.getUser())
        );
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public UserIdAndUsernameDto getUserDto() {
        return userDto;
    }
}
