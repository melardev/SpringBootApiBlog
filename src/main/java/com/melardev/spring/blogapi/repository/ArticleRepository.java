package com.melardev.spring.blogapi.repository;

import com.melardev.spring.blogapi.entities.Article;
import com.melardev.spring.blogapi.entities.Tag;
import com.melardev.spring.blogapi.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    //Page<Article> findByCreatedAtDesc(Pageable pageable);

    @Query("SELECT a FROM Article a INNER JOIN a.categories c WHERE c.name=:category")
    Page<Article> findByCategory(@Param("category") String category, Pageable pageable);

    @Query("SELECT new com.melardev.spring.blogapi.entities.extensions.ArticleExtension(a.id, a.title,a.slug, a.description,a.user.id,a.user.username, a.publishOn, a.comments.size) FROM Article a inner join a.categories c WHERE c.slug = :slug")
    Page<Article> findByCategorySlug(String slug, Pageable pageable);

    @Query("SELECT a FROM Article a INNER JOIN a.tags t WHERE t.name = :tag")
    Page<Article> findByTagFull(@Param("tag") String tag, Pageable pageable);

    @Query("SELECT new com.melardev.spring.blogapi.entities.extensions.ArticleExtension(a.id, a.title,a.slug, a.description,a.user.id,a.user.username, a.publishOn, a.comments.size) FROM Article a inner join a.tags t WHERE t.slug = :slug")
    Page<Article> findByTagSlug(@Param("slug") String slug, Pageable pageable);

    Article findByUser(User user);

    // Search by body or title
    List<Article> findByBodyIsLikeOrTitleIsLike(String term, String term2);

    //Find posts between today and 3 days ahead
    List<Article> findByCreatedAtBetween(Date from, Date to);


    // Get the list of posts in reverse order.
    @Query("select a from Article a order by a.id desc")
    List<Article> articlesInReverseOrder();

    List<Article> findAllByTags(List<Tag> tags);

    @Query(value = "SELECT * FROM articles order by rand() limit 1", nativeQuery = true)
    Article getRandom();

    @Query("select new com.melardev.spring.blogapi.entities.extensions.ArticleExtension(a.id) from Article a where a.user.id <> :userId")
    List<Article> getArticlesNotAuthoredBy(@Param("userId") Long userId);

    // @Query("select new com.melardev.spring.blogapi.entities.extensions.ArticleExtension(a.id) from Article a where a.user.id <> :userId")
    // List<Article> getArticlesNotLikedBy(@Param("userId") Long userId);

    // This works, but for a performance boot, load the username and user id from the author
    Article findBySlug(String slug);

    @Query("select new com.melardev.spring.blogapi.entities.extensions.ArticleExtension(a.id, a.title, a.slug, a.description, a.body," +
            // " a.comments," +
            // " count(a.likes)," +
            // " a.tags, a.categories, a.createdAt, a.updatedAt, a.views," +
            " a.user.id, a.user.username) from Article a where a.slug=:slug")
    Article findBySlugPerformance(@Param("slug") String slug);

    @Query("from Article a where a.user.id in :userIds")
    List<Article> findAllForFeed(@Param("userIds") List<Long> userIds);

    @Query("from Article a where a.user.id in :userIds")
    Page<Article> findAllForFeed(@Param("userIds") List<Long> userIds, Pageable pageRequest);

    @Query("select a from Article a where a.user.id in (select user.id from User user inner join com.melardev.spring.blogapi.entities.UserSubscription relation on user.id=relation.following.id where relation.follower.id=:userId)")
    Page<Article> findAllForFeed(@Param("userId") Long userId, Pageable pageable);

    @Query("select a from Article a where a.user.id in (select user.id from User user inner join com.melardev.spring.blogapi.entities.UserSubscription relation on user.id=relation.following.id where relation.follower.id=:userId)")
    List<Article> findAllForFeed(@Param("userId") Long userId);

    @Query("select a.id from Article a")
    List<Long> findAllIds();

    @Query("select new com.melardev.spring.blogapi.entities.extensions.ArticleExtension(a.id) from Article a where a.id =:articleId")
    Article getProxyForSave(@Param("articleId") Long articleId);

    @Query("select new com.melardev.spring.blogapi.entities.extensions.ArticleExtension(a.id, a.title, a.slug, a.description,a.user.id, a.user.username, a.publishOn) from Article a")
    Page<Article> findAllForSummary(PageRequest pageRequest);

    @Query("select a.id from Article a where a.slug=:slug")
    Long fetchIdFromSlug(String slug);

    @Query("select new com.melardev.spring.blogapi.entities.extensions.ArticleExtension(a.id) from Article a where a.slug =:slug")
    Article fetchProxyFromSlug(String slug);


    @Query("SELECT t.name, count(a) as tag_count from Article a " +
            "INNER JOIN a.tags t " +
            "WHERE a.id in :ids " +
            "GROUP BY t.id " +
            "ORDER BY tag_count DESC")
    List<Object[]> countArticlesByTags(@Param("ids") List<Long> articleIds);

}