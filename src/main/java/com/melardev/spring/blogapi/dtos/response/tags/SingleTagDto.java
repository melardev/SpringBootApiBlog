package com.melardev.spring.blogapi.dtos.response.tags;

import com.melardev.spring.blogapi.entities.Tag;

public class SingleTagDto {

    private String slug;
    private Long id;
    private String name;


    public SingleTagDto(Long id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }

    public static SingleTagDto build(Tag tag) {
        return new SingleTagDto(tag.getId(), tag.getName(), tag.getSlug());
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
