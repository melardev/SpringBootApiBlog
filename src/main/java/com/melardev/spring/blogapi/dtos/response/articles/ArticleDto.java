package com.melardev.spring.blogapi.dtos.response.articles;


import com.melardev.spring.blogapi.entities.Category;
import com.melardev.spring.blogapi.entities.Tag;
import com.melardev.spring.blogapi.enums.ContentType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;


public class ArticleDto {

    @Size(min = 2, max = 255, message = "{errors.article.title.size}")
    @NotNull(message = "{errors.article.title.null}")
    @NotEmpty(message = "{errors.article.title.empty}")
    public String title;

    public String slug;
    @Size(min = 10, max = 100, message = "{errors.article.description.size}")
    @NotNull(message = "{errors.article.description.null}")
    @NotEmpty(message = "{errors.article.description.empty}")
    private String description;

    @Size(min = 10, max = 100, message = "{errors.article.description.size}")
    @NotNull(message = "{errors.article.description.null}")
    @NotEmpty(message = "{errors.article.description.empty}")
    private String body;


    private ContentType contentType;
    //@NotNull Not implemented yet
    private Set<Tag> tags;

    //@NotNull Not implemented yet
    private Set<Category> categories;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public ArticleDto() {
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

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }
}
