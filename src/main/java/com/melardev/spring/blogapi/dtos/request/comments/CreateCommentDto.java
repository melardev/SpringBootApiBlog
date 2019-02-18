package com.melardev.spring.blogapi.dtos.request.comments;

import javax.validation.constraints.NotEmpty;

public class CreateCommentDto {

    @NotEmpty
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
