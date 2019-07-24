package com.melardev.spring.blogapi.entities.extensions;

import com.melardev.spring.blogapi.entities.Category;

public class CategoryExtension extends Category {

    private Long articleId;

    public CategoryExtension(String name, Long count) {
        this.name = name;
        this.articlesCount = count;
    }

    public CategoryExtension(Long id, String name, String slug, Long articleId) {
        this.articleId = articleId;
        this.name = name;
        this.slug = slug;
        this.id = id;
    }

    public CategoryExtension(Long id, String name, String slug) {
        this(id, name, slug, (long) -1);
    }

    public Long getArticleId() {
        return articleId;
    }
}
