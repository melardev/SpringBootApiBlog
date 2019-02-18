package com.melardev.spring.blogapi.dtos.response.tags;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TagDto {

    @Size(min = 2, max = 25)
    @NotNull(message = "{errors.tag.title.null}")
    @NotEmpty(message = "{errors.tag.title.empty}")
    private String name;

    @NotNull
    @Size(min = 2, max = 100)
    @NotEmpty(message = "{errors.tag.description.empty}")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}