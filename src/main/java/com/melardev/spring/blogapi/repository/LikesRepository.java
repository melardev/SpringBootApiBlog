package com.melardev.spring.blogapi.repository;

import com.melardev.spring.blogapi.entities.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Like, Long> {

    @Query("from Like l where l.article.slug=:slug and l.user.username=:username")
    Optional<Like> findByArticleSlugAndUsername(String slug, String username);

    @Query("from Like l where l.article.slug=:slug and l.user.id=:id")
    Optional<Like> findByArticleSlugAndUserId(String slug, Long id);

    @Query("from Like l where l.article.slug=:slug")
    List<Like> findAllFromArticle(String slug);

    @Query("from Like l where l.article.slug=:slug")
    Page<Like> findAllFromArticle(@Param("slug") String slug, Pageable pageable);

    @Query("from Like l where l.user.username=:username")
    List<Like> findAllFromUser(String username);

    @Query("from Like l where l.user.id=:id")
    List<Like> findAllFromUser(Long id);

    @Query("from Like l where l.user.username=:username")
    Page<Like> findAllFromUser(String username, Pageable pageable);

    @Query("from Like l where l.user.id=:id")
    Page<Like> findAllFromUser(Long id, Pageable pageable);

    @Query("select l.article.id from Like l where l.user.id <> :userId")
    List<Long> findArticlesNotLikedBy(@Param("userId") Long userId);

    @Query("select l.article.id from Like l where l.user.id = :userId")
    List<Long> findArticlesLikedBy(@Param("userId") Long userId);


    @Query("select l from Like l where l.userId=:userId and l.articleId = :articleId")
    Like findByUserIdAndArticleId(Long userId, Long articleId);
}
