package com.melardev.spring.blogapi.entities;

import javax.persistence.*;

@Entity
@Table(name = "likes", uniqueConstraints =
@UniqueConstraint(columnNames = {"user_id", "article_id"}))
public class Like extends TimestampedEntity {

    @Column(name = "user_id", insertable = false, updatable = false)
    Long userId;

    @Column(name = "article_id", insertable = false, updatable = false)
    Long articleId;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }
}
