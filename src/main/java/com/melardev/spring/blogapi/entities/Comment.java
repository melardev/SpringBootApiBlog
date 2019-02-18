package com.melardev.spring.blogapi.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "comments")
public class Comment extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;


    @ManyToOne
    @JoinColumn(name = "user_id") // not required
    private User user;

    @ManyToOne
    @JoinColumn(name = "article_id") // not required
    private Article article;

    private String content;

    // This is one approach to implementing Self referencing in a One to One relationship in Hibernate
    @ManyToOne(optional = true)
    @JoinColumn(name = "replied_comment_id", referencedColumnName = "id", nullable = true, table = "comments")
    private Comment repliedComment;

    @OneToMany(mappedBy = "repliedComment")
    private Collection<Comment> replies;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Comment getRepliedComment() {
        return repliedComment;
    }

    public void setRepliedComment(Comment repliedComment) {
        this.repliedComment = repliedComment;
    }

    public Collection<Comment> getReplies() {
        return replies;
    }

    public void setReplies(Collection<Comment> replies) {
        this.replies = replies;
    }
}