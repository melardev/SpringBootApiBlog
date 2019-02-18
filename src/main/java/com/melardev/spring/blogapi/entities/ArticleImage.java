package com.melardev.spring.blogapi.entities;

import javax.persistence.*;

@Entity
@DiscriminatorValue("0")
public class ArticleImage extends FileUpload {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}
