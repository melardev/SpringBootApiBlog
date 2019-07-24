package com.melardev.spring.blogapi.repository;

import com.melardev.spring.blogapi.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.user.id = :id")
    Page<Comment> findByUser(@Param("id") Long id, Pageable pageRequest);

    @Query("select count(c) from Comment c where c.repliedComment != null")
    int getRepliesCount();

    @Query(value = "SELECT * FROM comments order by rand() limit 1", nativeQuery = true)
    Comment getRandom();

    @Query(value = "SELECT c FROM Comment c where c.article.slug=:slug")
    Page<Comment> findByArticleSlug(@Param("slug") String slug, Pageable pageable);

    @Query("select c.article.id, count(*) from Comment c where c.article.id in :articleIds group by c.article.id")
    List<Object[]> fetchCommentCountForArticleIds(List<Long> articleIds);
}
