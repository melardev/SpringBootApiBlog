package com.melardev.spring.blogapi.services;

import com.melardev.spring.blogapi.entities.Article;
import com.melardev.spring.blogapi.entities.Like;
import com.melardev.spring.blogapi.entities.User;
import com.melardev.spring.blogapi.repository.LikesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class LikesService {

    @Autowired
    LikesRepository likesRepository;

    @Autowired
    ArticlesService articlesService;

    public long count() {
        return likesRepository.count();
    }

    public Page<Like> fetchPageByUser(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "createdAt");
        return this.likesRepository.findAll(pageRequest);
    }

    public Page<Like> fetchPageByUser(User user, int page, int pageSize) {
        return fetchPageByUser(user.getId(), page, pageSize);
    }

    public Page<Like> fetchPageByUser(Long userId, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "createdAt");
        return this.likesRepository.findAllFromUser(userId, pageRequest);
    }

    public Page<Like> findAllFromArticle(String slug, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "createdAt");
        return likesRepository.findAllFromArticle(slug, pageRequest);
    }

    public List<Like> findAllFromArticle(String slug) {
        return likesRepository.findAllFromArticle(slug);
    }

    public List<Long> findArticlesNotLikedBy(Long userId) {
        return likesRepository.findArticlesNotLikedBy(userId);
    }

    public List<Long> findArticlesLikedBy(Long userId) {
        return likesRepository.findArticlesLikedBy(userId);
    }

    public void saveAll(Set<Like> likes) {
        likesRepository.saveAll(likes);
    }

    public Like save(Like like) {
        return likesRepository.save(like);
    }

    public Like create(String slug, User user) {
        return create(articlesService.getArticleBySlug(slug), user);
    }

    public Like create(Article article, User user) {
        Like like = new Like();
        like.setArticle(article);
        like.setUser(user);
        return likesRepository.save(like);
    }

    public Like create(User user, Article article) {

        Like like = new Like();
        like.setArticle(article);
        like.setUser(user);
        return likesRepository.save(like);
    }

    public boolean isUserLikingArticle(Long userId, Long articleId) {
        return likesRepository.findByUserIdAndArticleId(userId, articleId) != null;
    }

    public Like fetchUserLikingArticle(Long userId, Long articleId) {
        return likesRepository.findByUserIdAndArticleId(userId, articleId);
    }

    public boolean isUserNotLikingArticle(Long userId, Long articleId) {
        return !isUserLikingArticle(userId, articleId);
    }


    public boolean delete(Article article, User user) {
        Optional<Like> like = likesRepository.findByArticleSlugAndUserId(article.getSlug(), user.getId());
        if (like.isPresent()) {
            likesRepository.delete(like.get());
            return true;
        } else {
            return false;
        }
    }

    public boolean delete(String slug, User user) {
        return delete(articlesService.getArticleBySlug(slug), user);
    }


    public boolean deleteAllFromArticle(Article article) {
        List<Like> likes = likesRepository.findAllFromArticle(article.getSlug());
        likesRepository.deleteAll(likes);
        return true;
    }

    public boolean deleteAllFromUser(User user) {
        List<Like> likes = likesRepository.findAllFromUser(user.getId());
        likesRepository.deleteAll(likes);
        return true;
    }

    public boolean delete(Like like) {
        likesRepository.delete(like);
        return true;
    }
}
