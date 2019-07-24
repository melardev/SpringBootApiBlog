package com.melardev.spring.blogapi.controllers;


import com.melardev.spring.blogapi.dtos.response.categories.CategoriesListResponse;
import com.melardev.spring.blogapi.dtos.response.tags.TagsListResponse;
import com.melardev.spring.blogapi.entities.Category;
import com.melardev.spring.blogapi.entities.Tag;
import com.melardev.spring.blogapi.services.CategoriesService;
import com.melardev.spring.blogapi.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@CrossOrigin
@RestController
public class TagsAndCategoriesController {

    @Autowired
    TagService tagService;

    @Autowired
    CategoriesService categoryService;


    @GetMapping("tags")
    public TagsListResponse getTags(@RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "page_size", defaultValue = "8") int pageSize,
                                    HttpServletRequest request) {
        Collection<Tag> tags = tagService.fetchNameAndSlug();
        return TagsListResponse.build(tags, request.getRequestURI());
    }


    @GetMapping("categories")
    public CategoriesListResponse getCategories(@RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "page_size", defaultValue = "8") int pageSize,
                                                HttpServletRequest request) {
        Collection<Category> categories = categoryService.fetchNameAndSlug();
        return CategoriesListResponse.build(categories, request.getRequestURI());
    }
}
