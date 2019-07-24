package com.melardev.spring.blogapi.services;

import com.melardev.spring.blogapi.entities.Category;
import com.melardev.spring.blogapi.repository.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoriesService {
    @Autowired
    CategoriesRepository categoriesRepository;

    public List<Category> getAllSummary() {
        return categoriesRepository.getAllSummary();
    }


    public Category findOrCreate(String name, String description) {
        Category category = categoriesRepository.findByNameIgnoreCase(name);
        if (category == null) {
            category = categoriesRepository.save(new Category(name, description));
        }
        return category;
    }

    public Category findOrCreate(Category category) {
        Category c = categoriesRepository.findByNameIgnoreCase(category.getName());
        if (c == null)
            c = categoriesRepository.save(new Category(category.getName(), category.getDescription()));

        return c;
    }

    public Set<Category> getOrCreate(Set<Category> categories) {
        if (categories == null) return null;
        categories = categories.stream().map(category -> category = findOrCreate(category)).collect(Collectors.toSet());
        return categories;
    }

    public List<Category> findAll() {
        return categoriesRepository.findAll();
    }

    public List<Category> getNamesForProductIds(List<Long> productIds) {
        return categoriesRepository.fetchCategorySummaryFromArticles(productIds);
    }

    public Collection<Category> fetchNameAndSlug() {
        return categoriesRepository.fetchNameAndSlug();
    }

    public Set<Category> getNamesForProductId(Long id) {
        return categoriesRepository.fetchCategoriesFromArticleId(id);
    }
}
