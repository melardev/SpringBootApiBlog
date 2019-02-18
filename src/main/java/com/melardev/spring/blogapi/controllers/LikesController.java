package com.melardev.spring.blogapi.controllers;

import com.melardev.spring.blogapi.dtos.response.base.AppResponse;
import com.melardev.spring.blogapi.dtos.response.base.ErrorResponse;
import com.melardev.spring.blogapi.dtos.response.base.SuccessResponse;
import com.melardev.spring.blogapi.dtos.response.likes.LikesResponse;
import com.melardev.spring.blogapi.entities.Article;
import com.melardev.spring.blogapi.entities.Like;
import com.melardev.spring.blogapi.entities.User;
import com.melardev.spring.blogapi.services.ArticlesService;
import com.melardev.spring.blogapi.services.LikesService;
import com.melardev.spring.blogapi.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LikesController {

    private final LikesService likesService;
    private final UsersService usersService;


    @Autowired
    WebApplicationContext context;
    @Autowired
    private ArticlesService articleService;

    @Autowired
    public LikesController(LikesService likesService, UsersService usersService) {
        this.likesService = likesService;
        this.usersService = usersService;
    }

    @GetMapping("likes")
    public LikesResponse index(@RequestParam(value = "page", defaultValue = "1") int page,
                               HttpServletRequest request,
                               @RequestParam(value = "page_size", defaultValue = "30") int pageSize) {
        Page<Like> pagedLikes = likesService.fetchPageByUser(usersService.getCurrentLoggedInUser(), page, pageSize);
        return LikesResponse.build(pagedLikes, request.getRequestURL().toString());
    }


    @GetMapping("articles/{slug}/likes")
    public LikesResponse pagedlikes(@PathVariable(name = "slug", required = false) String slug,
                                    HttpServletRequest request,
                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "page_size", defaultValue = "30") int pageSize) {
        Page<Like> likes = likesService.findAllFromArticle(slug, page, pageSize);
        return LikesResponse.build(likes, request.getRequestURI());
    }

    @PostMapping("articles/{slug}/likes")
    public ResponseEntity<AppResponse> create(@PathVariable("slug") String slug) {
        User user = usersService.getCurrentLoggedInUser();
        if (user == null)
            return new ResponseEntity<>(new ErrorResponse("You have to be authenticated"), HttpStatus.FORBIDDEN);
        Article article = articleService.fetchProxyFromSlug(slug);
        if (article != null && likesService.isUserNotLikingArticle(user.getId(), article.getId())) {
            Like like = likesService.create(user, article);
            return new ResponseEntity<>(new SuccessResponse("You are liking that article now!"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ErrorResponse("Either the article does not exist, or you are already liking it"), HttpStatus.OK);
        }
    }

    @DeleteMapping("articles/{slug}/likes")
    public AppResponse destroy(@PathVariable("slug") String slug) {
        User user = usersService.getCurrentLoggedInUser();

        if (user == null)
            return new ErrorResponse("You have to be authenticated");

        Long articleId = articleService.fetchIdFromSlug(slug);
        Like like = likesService.fetchUserLikingArticle(user.getId(), articleId);
        if (like != null) {
            boolean success = likesService.delete(like);
            if (success)
                return new SuccessResponse("Like deleted successfully");
            else
                return new ErrorResponse("Failed");
        } else {
            return new ErrorResponse("Permission denied, you are not liking this article");
        }
    }
}
