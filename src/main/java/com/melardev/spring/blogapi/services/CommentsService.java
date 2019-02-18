package com.melardev.spring.blogapi.services;


import com.melardev.spring.blogapi.entities.Article;
import com.melardev.spring.blogapi.entities.Comment;
import com.melardev.spring.blogapi.entities.User;
import com.melardev.spring.blogapi.errors.exceptions.ResourceNotFoundException;
import com.melardev.spring.blogapi.repository.CommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CommentsService {
    private CommentsRepository commentsRepository;

    @Autowired
    public CommentsService(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }


    public Page<Comment> getLatest(int page, int count) {
        PageRequest pageRequest = PageRequest.of(page, count, Sort.Direction.DESC, "createdAt");
        Page<Comment> result = this.commentsRepository.findAll(pageRequest);
        return result;
    }


    public Comment create(String content, Article article, User user) {
        Comment comment = new Comment();
        comment.setId(null);
        comment.setContent(content);
        comment.setArticle(article);
        comment.setUser(user);
        return this.commentsRepository.save(comment);
    }

    public Comment findById(Long id) {
        return getOrThrow(id);
    }

    private Comment getOrThrow(Long id) {
        return findById(id, true);
    }

    private Comment findById(Long id, boolean shouldThrow) {
        if (id == null && shouldThrow)
            throw new ResourceNotFoundException();
        else if (id == null)
            return null;
        Optional<Comment> comment = this.commentsRepository.findById(id);
        if (shouldThrow)
            return comment.orElseThrow(ResourceNotFoundException::new);

        return comment.orElse(null);
    }

    public Page<Comment> getCommentsFromUserWithId(Long id, int page, int count) {
        PageRequest pageRequest = PageRequest.of(page - 1, count, Sort.Direction.DESC, "createdAt");
        return this.commentsRepository.findByUser(id, pageRequest);
    }

    public Comment findByIdNotThrow(Long id) {
        return findById(id, false);
    }

    public void delete(Comment comment) {
        this.commentsRepository.delete(comment);
    }

    public Comment update(Comment comment) {
        return this.commentsRepository.save(comment);
    }

    public List<Comment> findAll() {
        return commentsRepository.findAll();
    }

    public List<Comment> saveAll(Set<Comment> products) {
        return commentsRepository.saveAll(products);
    }

    public int getRepliesCount() {
        return commentsRepository.getRepliesCount();
    }

    public Comment getRandom() {
        return commentsRepository.getRandom();
    }

    public Page<Comment> getCommentsFromArticle(String slug, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "createdAt");
        return commentsRepository.findByArticleSlug(slug, pageRequest);
    }

    public List<Object[]> fetchCommentCountForArticleIds(List<Long> productIds) {
        return commentsRepository.fetchCommentCountForArticleIds(productIds);
    }
}
