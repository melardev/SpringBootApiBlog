package com.melardev.spring.blogapi.entities.extensions;

import com.melardev.spring.blogapi.entities.Tag;

public class TagExtension extends Tag {

    private Long articleId;

    public TagExtension(Long id, String name, String slug, Long articleId) {
        this.name = name;
        this.slug = slug;
        this.id = id;
        this.articleId = articleId;
    }

    public TagExtension(String name, Long count) {
        this.name = name;
        this.articlesCount = count;
    }

    public TagExtension(Long id, String name, String slug) {
        this(id, name, slug, (long) -1);
    }

    public TagExtension(String name, int count) {
        this.name = name;
        this.articlesCount = (long) count;
    }

    public Long getArticleId() {
        return articleId;
    }
}
