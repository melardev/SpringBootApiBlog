package com.melardev.spring.blogapi.repository;

import com.melardev.spring.blogapi.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface CategoriesRepository extends CrudRepository<Category, Long> {

    Page<Category> findAll(Pageable req);

    List<Category> findAll();

    Category findByNameIgnoreCase(String name);


    @Query("select new com.melardev.spring.blogapi.entities.extensions.CategoryExtension(c.name, count(a)) from Article a inner join a.categories c where c.name=:name")
    public List<Category> getArticlesCountCategorisedAs(@Param("name") String categoryName);

    @Query("select new com.melardev.spring.blogapi.entities.extensions.CategoryExtension(c.name, count(c.name)) from Category c inner join c.articles a group by c.name")
    List<Category> getAllSummary();

    @Query("select new com.melardev.spring.blogapi.entities.extensions.CategoryExtension(c.id, c.name,c.slug, a.id) from Category c inner join c.articles as a where a.id in :ids")
    List<Category> fetchCategorySummaryFromArticles(@Param("ids") List<Long> articleIds);

    @Query("select new com.melardev.spring.blogapi.entities.extensions.CategoryExtension(c.id, c.name, c.slug) from Category c")
    Collection<Category> fetchNameAndSlug();

    @Query("select c from Category c inner join c.articles a where a.id = :id")
    Set<Category> fetchCategoriesFromArticleId(Long id);
}
