package com.melardev.spring.blogapi.dtos.response.articles;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.melardev.spring.blogapi.dtos.response.categories.SingleCategoryDto;
import com.melardev.spring.blogapi.dtos.response.tags.SingleTagDto;
import com.melardev.spring.blogapi.dtos.response.users.partials.UserIdAndUsernameDto;
import com.melardev.spring.blogapi.entities.Article;
import com.melardev.spring.blogapi.entities.FileUpload;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ArticleSummaryDto {

    private Collection<String> images;
    private Long commentsCount;
    public long id;
    public String title;
    private String slug;
    private String description;
    @JsonProperty("user")
    private UserIdAndUsernameDto userDto;

    private List<SingleTagDto> tags;
    private List<SingleCategoryDto> categories;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    private ArticleSummaryDto(Long id, String title, String slug, String description, ZonedDateTime createdAt, ZonedDateTime updatedAt,
                              UserIdAndUsernameDto userDto) {

    }

    public ArticleSummaryDto(Long id, String title, String slug, String description, List<SingleTagDto> tags, List<SingleCategoryDto> categories,
                             Collection<String> imagePaths, Long commentCount,
                             ZonedDateTime publishOn, UserIdAndUsernameDto userDto) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.slug = slug;
        this.tags = tags;
        this.categories = categories;
        this.commentsCount = commentCount;
        this.createdAt = publishOn;
        this.userDto = userDto;
        this.images = imagePaths;

    }

    public static ArticleSummaryDto build(Article article) {
        return new ArticleSummaryDto(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getDescription(),
                article.getTags() != null ? article.getTags().stream().map(SingleTagDto::build).collect(toList()) : null,
                article.getCategories() != null ? article.getCategories().stream().map(SingleCategoryDto::build).collect(toList()) : null,
                article.getImages() != null ? article.getImages().stream().map(FileUpload::getFilePath).collect(toList()) : Collections.emptyList(),
                article.getCommentCount(),
                article.getPublishOn(),
                UserIdAndUsernameDto.build(article.getUser()));
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public ArticleSummaryDto(String title, String slug, double price) {
        this.title = title;
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SingleTagDto> getTags() {
        return tags;
    }

    public void setTags(List<SingleTagDto> tags) {
        this.tags = tags;
    }

    public List<SingleCategoryDto> getCategories() {
        return categories;
    }

    public void setCategories(List<SingleCategoryDto> categories) {
        this.categories = categories;
    }

    public Long getCommentsCount() {
        return commentsCount;
    }

    public UserIdAndUsernameDto getUserDto() {
        return userDto;
    }

    public Collection<String> getImages() {
        return images;
    }
}
