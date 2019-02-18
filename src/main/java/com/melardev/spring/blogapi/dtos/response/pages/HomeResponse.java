package com.melardev.spring.blogapi.dtos.response.pages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.melardev.spring.blogapi.dtos.response.articles.ArticleListResponse;
import com.melardev.spring.blogapi.dtos.response.base.SuccessResponse;
import com.melardev.spring.blogapi.dtos.response.categories.SingleCategoryDto;
import com.melardev.spring.blogapi.dtos.response.tags.SingleTagDto;
import com.melardev.spring.blogapi.entities.Article;
import com.melardev.spring.blogapi.entities.Category;
import com.melardev.spring.blogapi.entities.Tag;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class HomeResponse extends SuccessResponse {
    @JsonProperty("articles")
    private final ArticleListResponse articleDataSection;
    @JsonProperty("tags")
    private final List<SingleTagDto> tagDtos;
    @JsonProperty("categories")
    private final List<SingleCategoryDto> categoryDtos;

    public HomeResponse(ArticleListResponse articleDataSection, List<SingleTagDto> tagDtos, List<SingleCategoryDto> categoryDtos) {
        this.articleDataSection = articleDataSection;
        this.tagDtos = tagDtos;
        this.categoryDtos = categoryDtos;
    }

    public static HomeResponse build(Page<Article> articles, int page, int pageSize, String basePath,
                                     List<Tag> tags,
                                     List<Category> categories) {
        ArrayList<SingleTagDto> tagsDtos = new ArrayList<>();
        ArrayList<SingleCategoryDto> categoriesDtos = new ArrayList<>();
        for (Tag tag : tags) {
            tagsDtos.add(SingleTagDto.build(tag));
        }

        for (Category category : categories) {
            categoriesDtos.add(SingleCategoryDto.build(category));
        }
        return new HomeResponse(ArticleListResponse.build(articles, basePath),
                tagsDtos, categoriesDtos);
    }


    public ArticleListResponse getArticleDataSection() {
        return articleDataSection;
    }

    public List<SingleTagDto> getTagDtos() {
        return tagDtos;
    }

    public List<SingleCategoryDto> getCategoryDtos() {
        return categoryDtos;
    }
}
