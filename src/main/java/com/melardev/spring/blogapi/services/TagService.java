package com.melardev.spring.blogapi.services;

import com.melardev.spring.blogapi.entities.Tag;
import com.melardev.spring.blogapi.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class TagService {
    private TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag findOrCreateByName(String name) {
        return findOrCreate(name, "");
    }

    public Tag findOrCreate(String name, String description) {
        Tag tag = tagRepository.findByNameIgnoreCase(name);
        if (tag == null) {
            tag = tagRepository.save(new Tag(name, description));
        }
        return tag;
    }

    public Tag findOrCreate(Tag tag) {
        Tag t = tagRepository.findByNameIgnoreCase(tag.getName());
        if (t == null)
            t = tagRepository.save(new Tag(tag.getName(), tag.getDescription()));

        return t;
    }

    public Tag getTag(String tagName) {
        return tagRepository.findByNameIgnoreCase(tagName);
    }

    public void deleteTag(Tag tag) {
        tagRepository.delete(tag);
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public long getAllCount() {
        return tagRepository.count();
    }


    public List<Tag> getAllSummary() {
        return tagRepository.getAllSummary();
    }

    public Set<Tag> getOrCreate(Set<Tag> tags) {
        if (tags == null) return null;
        tags = tags.stream().map(t -> t = findOrCreate(t)).collect(Collectors.toSet());
        return tags;
    }

    public List<Tag> findTagNamesForArticleIds(List<Long> articleIds) {
        return tagRepository.fetchTagSummaryFromArticles(articleIds);
    }

    public Collection<Tag> fetchNameAndSlug() {
        return tagRepository.fetchNameAndSlug();
    }

    public Set<Tag> findTagNamesForArticleId(Long id) {
        return tagRepository.fetchTagsFromArticleId(id);
    }
}
