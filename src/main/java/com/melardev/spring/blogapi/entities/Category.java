package com.melardev.spring.blogapi.entities;

import com.melardev.spring.blogapi.services.StringHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "categories")
public class Category extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Column(nullable = false)
    protected String name;
    @Column(nullable = false, unique = true)
    protected String slug;

    private String description;

    @Transient
    protected Long articlesCount;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
    private List<Article> articles = new ArrayList<>();

    public Category() {

    }

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
