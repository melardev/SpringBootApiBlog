package com.melardev.spring.blogapi.dtos.response.comments;

import com.melardev.spring.blogapi.dtos.response.users.partials.UserIdAndUsernameDto;
import com.melardev.spring.blogapi.dtos.response.base.SuccessResponse;
import com.melardev.spring.blogapi.entities.Comment;

import java.time.ZonedDateTime;

public class SingleCommentDto extends SuccessResponse {
    private final ZonedDateTime updatedAt;
    private final ZonedDateTime createdAt;
    private final Long articleId;
    private final Long repliedCommentId;
    private final UserIdAndUsernameDto user;
    private Long id;

    private String content;


    public SingleCommentDto(Long id, String content, ZonedDateTime createdAt, ZonedDateTime updatedAt, Long articleId,
                            Long repliedCommentId,
                            UserIdAndUsernameDto user) {
        this.id = id;
        this.content = content;
        this.articleId = articleId;
        this.repliedCommentId = repliedCommentId;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static SingleCommentDto build(Comment comment) {
        // TODO: why getId() is always null, but .id is real id.
        return new SingleCommentDto(comment.id, comment.getContent(), comment.getCreatedAt(), comment.getUpdatedAt(),
                comment.getArticle().getId(),
                comment.getRepliedComment() != null ? comment.getRepliedComment().id : null,
                UserIdAndUsernameDto.build(comment.getUser()));
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getArticleId() {
        return articleId;
    }

    public Long getRepliedCommentId() {
        return repliedCommentId;
    }

    public UserIdAndUsernameDto getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
