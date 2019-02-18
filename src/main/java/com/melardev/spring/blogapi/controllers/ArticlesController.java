package com.melardev.spring.blogapi.controllers;


import com.melardev.spring.blogapi.dtos.response.articles.ArticleDto;
import com.melardev.spring.blogapi.dtos.response.articles.ArticleListResponse;
import com.melardev.spring.blogapi.dtos.response.articles.SingleArticleResponse;
import com.melardev.spring.blogapi.dtos.response.base.AppResponse;
import com.melardev.spring.blogapi.dtos.response.base.ErrorResponse;
import com.melardev.spring.blogapi.dtos.response.pages.HomeResponse;
import com.melardev.spring.blogapi.entities.Article;
import com.melardev.spring.blogapi.entities.Category;
import com.melardev.spring.blogapi.entities.Tag;
import com.melardev.spring.blogapi.entities.User;
import com.melardev.spring.blogapi.enums.CrudOperation;
import com.melardev.spring.blogapi.errors.exceptions.PermissionDeniedException;
import com.melardev.spring.blogapi.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("articles")
public class ArticlesController {

    private final ArticlesService articlesService;

    protected AuthorizationService authorizationService;

    @Autowired
    private UsersService usersService;


    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    private TagService tagService;


    @Autowired
    public ArticlesController(ArticlesService articlesService, AuthorizationService authorizationService) {
        super();
        this.authorizationService = authorizationService;
        this.articlesService = articlesService;

    }

    @GetMapping
    public ArticleListResponse index(@RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam(value = "page_size", defaultValue = "8") int pageSize,
                                     HttpServletRequest httpServletRequest) {

        Page<Article> articles = articlesService.findAllForSummary(page, pageSize);

        return ArticleListResponse.build(articles, httpServletRequest.getRequestURI());
    }

    @GetMapping("feed")
    public HomeResponse feed(@RequestParam(value = "page", defaultValue = "1") int page,
                             HttpServletRequest request,
                             @RequestParam(value = "page_size", defaultValue = "8") int pageSize) {
        User user = usersService.getCurrentLoggedInUser();
        if (user == null)
            throw new PermissionDeniedException("You should be logged In to have a feed");
        Page<Article> articles = articlesService.getFeed(user, page, pageSize);
        List<Category> categories = categoriesService.getAllSummary();
        List<Tag> tags = tagService.getAllSummary();

        return HomeResponse.build(articles, page, pageSize, request.getRequestURI(), tags, categories);
    }


    @GetMapping("by_tag/{tag_slug}")
    public ArticleListResponse getArticlesByTag(@PathVariable(name = "tag_slug", required = false) String tagName,
                                                HttpServletRequest request,
                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "page_size", defaultValue = "30") int pageSize) {
        Page<Article> articlesPage = articlesService.getByTagSlug(tagName, page, pageSize);
        return ArticleListResponse.build(articlesPage, request.getRequestURI());
    }


    @GetMapping("{slug}")
    public SingleArticleResponse show(@PathVariable("slug") String slug) {
        Article article = articlesService.getRenderedArticleBySlug(slug);
        return SingleArticleResponse.build(article);
    }

    @GetMapping("by_id/{id}")
    public SingleArticleResponse show(@PathVariable("id") Long id) {
        Article article = articlesService.getArticleById(id);
        return SingleArticleResponse.build(article);
    }

    @PostMapping
    public ResponseEntity<AppResponse> create(Principal principal, @Valid @RequestBody ArticleDto articleDto, Errors errors, BindingResult bindingResult) {
        if (!this.authorizationCheck(CrudOperation.CREATE))
            throw new PermissionDeniedException("You are not allowed to create articles");

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new ErrorResponse(bindingResult.getModel()), HttpStatus.BAD_REQUEST);
        } else {
            Article article = articlesService.createArticle(
                    usersService.getCurrentLoggedInUser(),
                    articleDto.getTitle(),
                    articleDto.getSlug(),
                    articleDto.getDescription(),
                    articleDto.getBody(),
                    articleDto.getContentType(),
                    articleDto.getTags(),
                    articleDto.getCategories()
            );
            return new ResponseEntity<>(SingleArticleResponse.build(article), HttpStatus.CREATED);
        }
    }

    @PutMapping("{slug}")
    public ResponseEntity<AppResponse> update(@PathVariable("slug") String slug, @Valid @RequestBody ArticleDto articleDto, Errors errors, BindingResult bindingResult) {

        Article article = articlesService.getArticleBySlug(slug);
        if (!this.authorizationCheck(CrudOperation.UPDATE, article))
            throw new PermissionDeniedException("You are not allowed to create articles");

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new ErrorResponse(bindingResult.getModel()), HttpStatus.BAD_REQUEST);
        } else {
            Article updatedArticle = articlesService.update(
                    usersService.getCurrentLoggedInUser(),
                    article,
                    articleDto.getTitle(),
                    articleDto.getSlug(),
                    articleDto.getDescription(),
                    articleDto.getBody(),
                    articleDto.getContentType(),
                    articleDto.getTags(),
                    articleDto.getCategories()
            );
            return new ResponseEntity<>(SingleArticleResponse.build(updatedArticle), HttpStatus.CREATED);
        }
    }

    private boolean authorizationCheck(CrudOperation operation) {
        return this.authorizationCheck(operation, null);
    }

    private boolean authorizationCheck(CrudOperation operation, Article article) {
        User user = usersService.getCurrentLoggedInUser();
        switch (operation) {
            case CREATE:
                return this.authorizationService.canCreateArticles(user);
            case UPDATE:
                return this.authorizationService.canUpdateArticles(article, usersService.getCurrentLoggedInUser());
            case DELETE:
                return this.authorizationService.canDeleteArticles(article, usersService.getCurrentLoggedInUser());
            default:
                return false;
        }
    }
}