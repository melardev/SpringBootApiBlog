package com.melardev.spring.blogapi.controllers;


import com.melardev.spring.blogapi.dtos.request.comments.CreateCommentDto;
import com.melardev.spring.blogapi.dtos.response.base.AppResponse;
import com.melardev.spring.blogapi.dtos.response.base.ErrorResponse;
import com.melardev.spring.blogapi.dtos.response.base.SuccessResponse;
import com.melardev.spring.blogapi.dtos.response.comments.CommentListDto;
import com.melardev.spring.blogapi.dtos.response.comments.SingleCommentDto;
import com.melardev.spring.blogapi.entities.Article;
import com.melardev.spring.blogapi.entities.Comment;
import com.melardev.spring.blogapi.entities.User;
import com.melardev.spring.blogapi.enums.CrudOperation;
import com.melardev.spring.blogapi.errors.exceptions.PermissionDeniedException;
import com.melardev.spring.blogapi.services.ArticlesService;
import com.melardev.spring.blogapi.services.AuthorizationService;
import com.melardev.spring.blogapi.services.CommentsService;
import com.melardev.spring.blogapi.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class CommentsController {

    private AuthorizationService authorizationService;
    private CommentsService commentsService;
    private UsersService usersService;
    private ArticlesService articlesService;

    @Autowired
    public CommentsController(CommentsService commentsService,
                              ArticlesService articlesService,
                              AuthorizationService authorizationService,
                              UsersService usersService) {
        this.commentsService = commentsService;
        this.articlesService = articlesService;
        this.authorizationService = authorizationService;
        this.usersService = usersService;
    }

    @GetMapping("/articles/{slug}/comments")
    public CommentListDto index(@PathVariable("slug") String slug,
                                HttpServletRequest request,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "page_size", defaultValue = "30") int pageSize) {

        Page<Comment> commentsPage = this.commentsService.getCommentsFromArticle(slug, page, pageSize);
        return CommentListDto.build(commentsPage, "");
    }

    @GetMapping({"/comments/{id}", "/articles/{slug}/comments/{id}"})
    public SingleCommentDto show(@PathVariable("id") Long id) {
        Comment comment = this.commentsService.findById(id);
        return SingleCommentDto.build(comment);
    }

    @GetMapping("/comments/from_user/{user_id}")
    public CommentListDto fromUser(@PathVariable("user_id") Long id, Model model,
                                   HttpServletRequest request,
                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "page_size", defaultValue = "30") int pageSize) {
        User user = this.usersService.findById(id);

        Page<Comment> pagedComments = this.commentsService.getCommentsFromUserWithId(user.getId(), page, pageSize);
        return CommentListDto.build(pagedComments, "");
    }


    @PostMapping("/articles/{slug}/comments")
    public ResponseEntity<AppResponse> create(@PathVariable("slug") String slug, @Valid @RequestBody CreateCommentDto form, BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return new ResponseEntity<>(new ErrorResponse(bindingResult.getModel()), HttpStatus.BAD_REQUEST);

        if (this.isNotAuthorized(CrudOperation.CREATE, null))
            throw new PermissionDeniedException("Permission denied");

        Article article = this.articlesService.getArticleBySlug(slug);
        User user = this.usersService.getCurrentLoggedInUser();
        Comment comment = this.commentsService.create(form.getContent(), article, user);

        return ResponseEntity.ok(SingleCommentDto.build(comment));
    }


    @PutMapping({"/comments/{id}", "/articles/{slug}/comments/{id}"})
    public ResponseEntity<AppResponse> update(@PathVariable("id") Long id, @RequestBody CreateCommentDto form) {
        if (this.isNotAuthorized(CrudOperation.UPDATE, id))
            throw new PermissionDeniedException("You are not allowed to this comment");

        Comment comment = this.commentsService.findByIdNotThrow(id);

        comment.setContent(form.getContent());
        comment = this.commentsService.update(comment);

        return ResponseEntity.ok(SingleCommentDto.build(comment));
    }


    @DeleteMapping({"/comments/{id}", "/articles/{slug}/comments/{id}"})
    public AppResponse delete(@PathVariable("id") Long id) {
        if (this.isNotAuthorized(CrudOperation.DELETE, id))
            throw new PermissionDeniedException("You are not allowed to delete comments");

        Comment comment = this.commentsService.findById(id);
        if (comment == null)
            throw new PermissionDeniedException();

        this.commentsService.delete(comment);
        return new SuccessResponse("Deleted Succesfully");
    }

    private boolean isNotAuthorized(CrudOperation operation, Long id) {
        return !isAuthorized(operation, id);
    }

    private boolean isAuthorized(CrudOperation operation, Long id) {
        return this.isAuthorized(this.commentsService.findByIdNotThrow(id), operation);
    }

    private boolean isAuthorized(Comment comment, CrudOperation operation) {
        switch (operation) {
            case CREATE:
                return this.authorizationService.canCreateComments();
            case UPDATE:
                return this.authorizationService.canUpdateComments(comment, usersService.getCurrentLoggedInUser());
            case DELETE:
                return this.authorizationService.canDeleteComments(comment);
            default:
                return false;
        }
    }
}