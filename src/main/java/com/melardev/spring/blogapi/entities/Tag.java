package com.melardev.spring.blogapi.entities;

import com.melardev.spring.blogapi.services.StringHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tags")
public class Tag extends TimestampedEntity {

    @Column(nullable = false)
    protected String name;
    @Column(nullable = false, unique = true)
    protected String slug;

    private String description;

    @Transient
    protected Long articlesCount;

    // mappedBy indicates this is not the owning side, it is the Article who owns the relationship
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")
    private List<Article> articles = new ArrayList<>();

    public Tag() {

    }

    public Tag(String name) {
        this.setName(name);
    }

    public Tag(String tagName, String tagDescription) {
        this.name = tagName;
        this.description = tagDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public Long getArticlesCount() {
        return articlesCount;
    }

    public void setArticlesCount(Long articlesCount) {
        this.articlesCount = articlesCount;
    }

    @PrePersist
    public void prePersist() {
        super.prePersist();
        if (StringHelper.isEmpty(this.slug))
            setSlug(StringHelper.slugify(getName()));
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
