package com.melardev.spring.blogapi.services;

import com.melardev.spring.blogapi.entities.*;
import com.melardev.spring.blogapi.entities.extensions.CategoryExtension;
import com.melardev.spring.blogapi.entities.extensions.TagExtension;
import com.melardev.spring.blogapi.entities.extensions.UploadExtension;
import com.melardev.spring.blogapi.enums.ContentType;
import com.melardev.spring.blogapi.errors.exceptions.ResourceNotFoundException;
import com.melardev.spring.blogapi.repository.ArticleRepository;
import com.melardev.spring.blogapi.repository.UploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticlesService {

    @Autowired
    ArticleRepository articlesRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private UploadRepository uploadRepository;

    public Set<Tag> getArticleTags(Long id) {
        // new PageRequest(page, pageSize, Sort.Direction.DESC, "createdAt")
        //return articlesRepository.findByTagSlug(tagName, pageMeta);
        Set<Tag> tags = new HashSet<Tag>();
        Optional<Article> article = articlesRepository.findById(id);
        if (!article.isPresent())
            throw new ResourceNotFoundException();

        article.get()
                .getTags()
                .forEach(tags::add);

        /*
        *
        // Load the post first. If not, when the post is cached before while the tags not,
        // then the LAZY loading of post tags will cause an initialization error because
        // of not hibernate connection session
        articlesRepository.findOne(post.getId()).getTagDtos().forEach(tags::add);*/
        return tags;
    }

    public Page<Article> getByTagSlug(String slug, int page, int count) {
        PageRequest pageRequest = PageRequest.of(page - 1, count, Sort.Direction.DESC, "createdAt");
        Page<Article> articles = articlesRepository.findByTagSlug(slug, pageRequest);
        return populateTagsAndCategories(articles);
    }

    public Page<Article> findAllForSummary(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "createdAt");
        Page<Article> result = this.articlesRepository.findAllForSummary(pageRequest);
        // Why not just calling this.articlesRepository.fetchPageByUser(pageRequest) ? because the below code is faster
        // but you could call fetchPageByUser() and then you will also need to make a request to comments table to fetch comments count
        List<Article> articles = result.getContent();
        List<Long> articleIds = articles.stream().map(p -> p.getId()).collect(Collectors.toList());

        List<Tag> tags = tagService.findTagNamesForArticleIds(articleIds);
        List<Category> categories = categoriesService.getNamesForProductIds(articleIds);
        List<Object[]> commentCounts = commentsService.fetchCommentCountForArticleIds(articleIds);
        List<UploadExtension> images = uploadRepository.getImagesFromArticles(articleIds);

        // this is done to iterate once, instead of iterating all(MANY) the products for each(MANY), then for each category and then comment
        // I iterate in (ONE) pass over categories, tags, and commentsCount, then the products are iterated in nested loop (MANY)
        // that approach is implemented in OrderService::findOrderSummariesBelongingToUser orderItems.forEach blabla
        int lastIndex = Math.max(Math.max(commentCounts.size(), Math.max(tags.size(), categories.size())), images.size());

        TagExtension tag = null;
        CategoryExtension category = null;
        int index = -1;
        UploadExtension image = null;
        Article article;
        for (int i = 0; i < lastIndex; i++) {
            if (i < tags.size())
                tag = (TagExtension) tags.get(i);
            if (i < categories.size())
                category = (CategoryExtension) categories.get(i);

            if (commentCounts.size() - 1 > i)
                index = i;
            else
                index = -1;

            if (i < images.size())
                image = images.get(i);

            for (int j = 0; j < articles.size(); j++) {
                article = articles.get(j);

                if (tag != null && article.getId().equals(tag.getArticleId()))
                    article.getTags().add(tag);

                if (category != null && article.getId().equals(category.getArticleId()))
                    article.getCategories().add(category);

                if (image != null && article.getId().equals(image.getArticleId()))
                    article.getImages().add(new ArticleImage(image.getId(), image.getFilePath()));

                if (index != -1 && article.getId().equals(commentCounts.get(index)[0]))
                    article.setCommentsCount((Long) commentCounts.get(index)[1]);
            }
        }

        return result;
    }

    public Article update(Article article) {
        return this.articlesRepository.save(article);
    }


    public void delete(Long id) {
        this.articlesRepository.deleteById(id);
    }

    public void delete(Article article) {
        this.articlesRepository.delete(article);
    }


    public Article getArticleBySlug(String slug) {
        return getArticleBySlug(slug, true);
    }

    public Article getArticleBySlug(String slug, boolean throwIfNull) {
        Article article = articlesRepository.findBySlug(slug);
        if (article == null) {
            if (throwIfNull)
                throw new ResourceNotFoundException("Article not found");
            return null;
        }

        Set<Tag> tags = tagService.findTagNamesForArticleId(article.getId());
        Set<Category> categories = categoriesService.getNamesForProductId(article.getId());

        article.setTags(tags);
        article.setCategories(categories);
        return article;
    }

    public Article getRenderedArticleBySlug(String slug) {
        Article article = getArticleBySlug(slug);
        if (article.getContentType() == ContentType.MARKDOWN) {
            article.setDescription(StringHelper.markdownToHtml(article.getDescription()));
            article.setBody(StringHelper.markdownToHtml(article.getBody()));
        }

        return article;
    }

    public Article getArticleById(Long id) {

        Optional<Article> article = articlesRepository.findById(id);
        if (article.isEmpty())
            throw new ResourceNotFoundException();

        return article.get();
    }

    public Article getReference(String slug) {
        //articlesRepository.findOne(slug);
        return null;
    }

    public List<Article> getFeed(User user) {
        return articlesRepository.findAllForFeed(user.getId());
    }

    public Page<Article> getFeed(User user, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "createdAt");
        return articlesRepository.findAllForFeed(user.getId(), pageRequest);
    }

    public Page<Article> getFeedBad(User user, int page, int pageSize) {
        List<Long> ids = user.getFollowingIds();
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt");
        return articlesRepository.findAllForFeed(ids, pageRequest);
    }

    public List<Article> getFeedBad(User user) {
        List<Long> ids = user.getFollowingIds();
        if (ids.size() == 0)
            return null;
        user.getFollowing().forEach(f -> ids.add(f.getId()));
        return articlesRepository.findAllForFeed(ids);
    }


    public Article getArticleReference(Long articleId) {
        // log.debug("Get post " + postId);

        Article article = articlesRepository.getOne(articleId);

        if (article == null)
            throw new ResourceNotFoundException();

        return article;
    }


    public Article createArticle(Article article) {
        return articlesRepository.save(article);
    }

    public Article updatePost(Article post) {
        return articlesRepository.save(post);
    }

    public void deletePost(Article post) {
        articlesRepository.delete(post);
    }

    /*
    public List<Article> getArchivePosts() {
        //   log.debug("Get all archive posts from database.");

        Pageable page = PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.DESC, "createdAt");
        return articlesRepository.findAllByContentTypeAndPostStatus(PostType.POST, PostStatus.PUBLISHED, page)
                .getContent()
                .stream()
                .map(this::extractPostMeta)
                .collect(Collectors.toList());
    }
    */


    // cache or not?
    public Page<Article> getArticlesbyTagName(String tagName, int page, int pageSize) {
        return articlesRepository.findByTagSlug(tagName, PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt"));
    }


    @Async
    public void incrementViews(Long postId) {
        synchronized (this) {
            Article article = articlesRepository.getOne(postId);
            article.setViews(article.getViews() + 1);
            articlesRepository.save(article);
        }
    }


    public Article getById(Long id) {

        Optional<Article> article = articlesRepository.findById(id);

        if (!article.isPresent()) {
            throw new ResourceNotFoundException();
        }

        return article.get();
    }


    public String getTagNames(Set<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }

        StringBuilder names = new StringBuilder();
        tags.forEach(tag -> names.append(tag.getName()).append(","));
        names.deleteCharAt(names.length() - 1);

        return names.toString();
    }

    // cache or not?
    public Page<Article> findPostsByTag(String tagName, int page, int pageSize) {
        return articlesRepository.findByTagSlug(tagName, PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt"));
    }

    public void saveAll(Set<Article> articles) {
        articlesRepository.saveAll(articles);
    }

    public List<Article> findAll() {
        return articlesRepository.findAll();
    }

    public Article getRandom() {
        return articlesRepository.getRandom();
    }

    public Article getRandomNotLikedBy(User user) {
        return null;
        // return articlesRepository.getArticlesNotLikedBy(user.getId());
    }

    public Article createArticle(User user, String title, String slug, String description, String body, ContentType
            contentType, Set<Tag> tags, Set<Category> categories, List<ArticleImage> imageList) {
        Article article = prepareArticle(user, title, slug, description, body, contentType, tags, categories, imageList);
        articlesRepository.save(article);
        for (ArticleImage img : imageList) {
            img.setArticle(article);
        }
        uploadRepository.saveAll(imageList);
        return article;
    }

    private Article prepareArticle(User user, String title, String slug, String description, String
            body, ContentType contentType, Set<Tag> tags, Set<Category> categories, List<ArticleImage> imageList) {
        Article article = new Article();
        return prepareArticle(user, article, title, slug, description, body, contentType, tags, categories, imageList);
    }

    private Article prepareArticle(User user, Article article, String title, String slug, String
            description, String body, ContentType
                                           contentType, Set<Tag> tags, Set<Category> categories, List<ArticleImage> imageList) {

        article.setTitle(title);
        article.setSlug(slug);
        article.setDescription(description);
        article.setBody(body);
        article.setContentType(contentType);
        tags = tagService.getOrCreate(tags);
        categories = categoriesService.getOrCreate(categories);
        article.setTags(tags);
        article.setCategories(categories);
        article.setUser(user);
        // article.setImages(imageList);
        return article;
    }

    public Article update(User user,
                          Article article,
                          String title,
                          String slug,
                          String description,
                          String body, ContentType contentType,
                          Set<Tag> tags,
                          Set<Category> categories
    ) {
        prepareArticle(user, article, title, slug, description, body, contentType, tags, categories, null);
        return articlesRepository.save(article);
    }


    public Article getProxy(Long id) {
        return articlesRepository.getOne(id);
    }

    public List<Long> fetchAllIds() {
        return articlesRepository.findAllIds();
    }

    public Article getProxyForSave(Long articleId) {
        return articlesRepository.getProxyForSave(articleId);
    }

    public Long fetchIdFromSlug(String slug) {
        return articlesRepository.fetchIdFromSlug(slug);
    }

    public Article fetchProxyFromSlug(String slug) {
        return articlesRepository.fetchProxyFromSlug(slug);
    }

    public Page<Article> getByCategorySlug(String categorySlug, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "createdAt");
        Page<Article> articles = articlesRepository.findByCategorySlug(categorySlug, pageRequest);
        return populateTagsAndCategories(articles);
    }

    private Page<Article> populateTagsAndCategories(Page<Article> articles) {
        List<Long> articleIds = articles.getContent().stream().map(TimestampedEntity::getId).collect(Collectors.toList());
        List<Tag> tags = tagService.findTagNamesForArticleIds(articleIds);
        List<Category> categories = categoriesService.getNamesForProductIds(articleIds);

        articles.stream().forEach(article -> {
            tags.forEach(tag -> {
                if (((TagExtension) tag).getArticleId().equals(article.getId()))
                    article.getTags().add(tag);
            });

            categories.forEach(category -> {
                if (((CategoryExtension) category).getArticleId().equals(article.getId()))
                    article.getCategories().add(category);
            });
        });
        return articles;
    }
}