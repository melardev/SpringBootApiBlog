package com.melardev.spring.blogapi.dtos.response.comments;

import com.melardev.spring.blogapi.dtos.response.shared.PageMeta;
import com.melardev.spring.blogapi.dtos.response.base.SuccessResponse;
import com.melardev.spring.blogapi.dtos.response.comments.partials.CommentPartialDto;
import com.melardev.spring.blogapi.entities.Comment;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class CommentListDto extends SuccessResponse {
    private final PageMeta pageMeta;
    private final List<CommentPartialDto> comments;

    public CommentListDto(PageMeta pageMeta, List<CommentPartialDto> commentPartialDtos) {
        this.pageMeta = pageMeta;
        this.comments = commentPartialDtos;
    }

    public static CommentListDto build(Page<Comment> commentsPage, String basePath) {
        List<CommentPartialDto> commentPartialDtos = new ArrayList<>();
        for (Comment comment : commentsPage.getContent()) {
            commentPartialDtos.add(CommentPartialDto.build(comment));
        }

        return new CommentListDto(
                PageMeta.build(commentsPage, basePath),
                commentPartialDtos
        );
    }

    public PageMeta getPageMeta() {
        return pageMeta;
    }

    public List<CommentPartialDto> getComments() {
        return comments;
    }
}
