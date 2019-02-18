package com.melardev.spring.blogapi.controllers;

import com.melardev.spring.blogapi.services.ArticlesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class HomeController {

    private final ArticlesService articlesService;

    @Autowired
    public HomeController(ArticlesService articlesService) {
        this.articlesService = articlesService;
    }

    @GetMapping
    public String index(Model model) {
        int page = 1;
        int pageSize = 10;
        model.addAttribute("products", articlesService.findAllForSummary(page, pageSize));
        return "home/index";
    }

    @GetMapping("/about")
    public String about() {
        return "home/about";
    }
}
