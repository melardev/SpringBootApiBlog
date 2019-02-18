package com.melardev.spring.blogapi.dtos.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LikeDtoRequest {

    @NotEmpty
    @NotNull
    public String slug;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
