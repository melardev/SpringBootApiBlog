package com.melardev.spring.blogapi.dtos.response.likes.partials;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.melardev.spring.blogapi.dtos.response.articles.partials.ArticleElementalDto;
import com.melardev.spring.blogapi.entities.Like;

public class LikeSummaryDto {

    @JsonProperty("article")
    private final ArticleElementalDto articleDto;

    public LikeSummaryDto(ArticleElementalDto articleDto) {
        this.articleDto = articleDto;
    }

    public static LikeSummaryDto build(Like like) {
        return new LikeSummaryDto(ArticleElementalDto.build(like.getArticle()));
    }

    public ArticleElementalDto getArticleDto() {
        return articleDto;
    }
}
