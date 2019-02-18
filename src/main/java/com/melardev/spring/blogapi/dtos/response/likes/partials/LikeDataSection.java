package com.melardev.spring.blogapi.dtos.response.likes.partials;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.melardev.spring.blogapi.dtos.response.shared.PageMeta;
import com.melardev.spring.blogapi.entities.Like;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class LikeDataSection {
    private final PageMeta pageMeta;
    @JsonProperty("likes")
    private final List<LikeSummaryDto> likeDtos;

    private LikeDataSection(PageMeta pageMeta, List<LikeSummaryDto> likeDtos) {
        this.pageMeta = pageMeta;
        this.likeDtos = likeDtos;
    }

    public static LikeDataSection build(Collection<Like> likes, String basePath) {
        return new LikeDataSection(null,
                likes.stream().map(LikeSummaryDto::build).collect(Collectors.toList())
        );
    }

    public static LikeDataSection build(Page<Like> pagedLikes, String basePath) {
        List<Like> likes = pagedLikes.getContent();
        return new LikeDataSection(
                PageMeta.build(pagedLikes, basePath),
                likes.stream().map(LikeSummaryDto::build).collect(Collectors.toList())
        );
    }

    public PageMeta getPageMeta() {
        return pageMeta;
    }

    public List<LikeSummaryDto> getLikeDtos() {
        return likeDtos;
    }
}
