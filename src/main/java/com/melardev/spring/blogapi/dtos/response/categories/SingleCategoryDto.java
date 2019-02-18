package com.melardev.spring.blogapi.dtos.response.categories;

import com.melardev.spring.blogapi.entities.Category;

public class SingleCategoryDto {

    private String slug;
    private Long id;
    private String name;


    public SingleCategoryDto(Long categoryId, String name, String slug) {
        this.id = categoryId;
        this.name = name;
        this.slug = slug;
    }

    public static SingleCategoryDto build(Category category) {
        return new SingleCategoryDto(category.getId(), category.getName(), category.getSlug());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }
}
