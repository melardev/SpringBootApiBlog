package com.melardev.spring.blogapi.repository;

import com.melardev.spring.blogapi.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;


@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByNameIgnoreCase(String name);

    // Object[0] = Spring Object[1] = 10
    //@Query("select new com.melardev.spring.blogapi.entities.extensions.TagExtension(t.title, count(t.title)) from Tag t inner join t.articles a  where t.title='Rails' group by t.title")
    //@Query("select new com.melardev.spring.blogapi.entities.extensions.TagExtension(t.title, count(t.title)) from Tag t inner join t.articles a  where t.title='Rails'")
    @Query("select new com.melardev.spring.blogapi.entities.extensions.TagExtension(t.name, count(a)) from Article a inner join a.tags t where t.name=:name")
    public List<Tag> getArticlesCountTaggedWith(@Param("name") String tagName);

    @Query("select new com.melardev.spring.blogapi.entities.extensions.TagExtension(t.name, count(t.name)) from Tag t inner join t.articles a group by t.name")
    List<Tag> getAllSummary();

    @Query("select new com.melardev.spring.blogapi.entities.extensions.TagExtension(t.id, t.name,t.slug, a.id) from Tag t inner join t.articles as a where a.id in :ids")
    List<Tag> fetchTagSummaryFromArticles(List<Long> ids);

    @Query("select new com.melardev.spring.blogapi.entities.extensions.TagExtension(t.id, t.name, t.slug) from Tag t")
    Collection<Tag> fetchNameAndSlug();

    @Query("select t from Tag t inner join t.articles a where a.id = :id")
    Set<Tag> fetchTagsFromArticleId(Long id);
}
