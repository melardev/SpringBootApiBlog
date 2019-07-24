package com.melardev.spring.blogapi.entities.extensions;


import com.melardev.spring.blogapi.entities.Article;

import java.time.ZonedDateTime;

public class ArticleExtension extends Article {

    public ArticleExtension(Long id) {
        this.id = id;
    }

    public ArticleExtension(String title, String description) {
        this.title = title;
        this.description = description;
    }


    public ArticleExtension(Long id, String title, String slug, String description, Long userId, String username, ZonedDateTime publishOn, int commentsCount) {
        this(id, title, slug, description, userId, username, publishOn);
        this.setCommentsCount(commentsCount);
    }

    public ArticleExtension(Long id, String title, String slug, String description, Long userId, String username, ZonedDateTime publishOn) {
        this.id = id;
        // this.images = new ArrayList<>(images);
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.user = new UserExtension(userId, username);
        this.publishOn = publishOn;
    }


    public ArticleExtension(Long id, String title, String slug, String description, String body,
                            // Collection<Comment> comments,
                            // long likes,
                            // Set<Tag> tags, Set<Category> categories, ZonedDateTime createdAt, ZonedDateTime updatedAt,
                            // int views,
                            Long userId, String username) {
        this.title = title;
        this.description = description;
        this.user = new UserExtension(userId, username);
    }

}
