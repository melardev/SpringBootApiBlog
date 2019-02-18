package com.melardev.spring.blogapi.dtos.request.articles;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateArticleDto {

    @Size(min = 2, max = 255, message = "{errors.article.title.size}")
    @NotNull(message = "{errors.article.title.null}")
    @NotEmpty(message = "{errors.article.title.empty}")
    public String title;

    @Size(min = 10, max = 100, message = "{errors.article.description.size}")
    @NotNull(message = "{errors.article.description.null}")
    @NotEmpty(message = "{errors.article.description.empty}")
    private String description;
}
