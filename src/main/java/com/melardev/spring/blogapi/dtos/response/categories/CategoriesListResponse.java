package com.melardev.spring.blogapi.dtos.response.categories;


import com.melardev.spring.blogapi.dtos.response.shared.PageMeta;
import com.melardev.spring.blogapi.dtos.response.shared.PageMetaResponse;
import com.melardev.spring.blogapi.entities.Category;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CategoriesListResponse extends PageMetaResponse {


    private final List<SingleCategoryDto> categories;

    public CategoriesListResponse(PageMeta pageMeta, List<SingleCategoryDto> categoyDtos) {
        super(pageMeta);
        this.categories = categoyDtos;
    }

    public static CategoriesListResponse build(Page<Category> tags, String basePath) {
        List<SingleCategoryDto> categoryDtos = tags.getContent().stream()
                .map(SingleCategoryDto::build)
                .collect(Collectors.toList());

        return new CategoriesListResponse(PageMeta.build(tags, basePath), categoryDtos);
    }

    public static CategoriesListResponse build(Collection<Category> tags, String basePath) {
        List<SingleCategoryDto> categoryDtos = tags.stream()
                .map(SingleCategoryDto::build)
                .collect(Collectors.toList());

        return new CategoriesListResponse(null, categoryDtos);
    }

    public List<SingleCategoryDto> getCategories() {
        return categories;
    }
}
