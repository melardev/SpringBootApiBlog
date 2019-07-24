package com.melardev.spring.blogapi.entities;


import com.melardev.spring.blogapi.enums.ContentType;
import com.melardev.spring.blogapi.services.StringHelper;
import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;


@Entity
@Table(name = "articles")
public class Article extends TimestampedEntity implements IUserOwnedResource {
    private static final SimpleDateFormat SLUG_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    public String description;

    @ManyToOne
    @JoinColumn(name = "user_id") // not required
    protected
    User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "article", cascade = CascadeType.REMOVE)
    private Collection<Comment> comments = new ArrayList<>();

    @NotBlank(message = "Title can't be empty.")
    @Size(min = 3, message = "A title must be at least 3 characters.")
    @Column(nullable = false)
    protected String title;

    protected String slug;


    @Type(type = "text")
    public String body;
    @Column(nullable = false)
    protected ZonedDateTime publishOn;
    // The owning side defines the Join Columns and tables
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "articles_tags",
            joinColumns = { // Owning side first(joinColumns:article), then the other side(inverseJoin: tag)
                    @JoinColumn(name = "article_id", nullable = false, referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "tag_id", nullable = false, referencedColumnName = "id")}
    )
    protected Set<Tag> tags = new HashSet<>();


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "article")
    // @Where(clause = "upload_type=0")
    protected List<ArticleImage> images = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "articles_categories",
            joinColumns = {
                    @JoinColumn(name = "article_id", nullable = false, referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "category_id", nullable = false, referencedColumnName = "id")}
    )
    protected Set<Category> categories = new HashSet<>();

    /*
        // This has been already mapped by User.likedArticles
        @ManyToMany(mappedBy = "likedArticles", fetch = FetchType.LAZY)
       /* @JoinTable(name = "likes",
                joinColumns = {@JoinColumn(name = "article_id", nullable = false, referencedColumnName = "id")}, inverseJoinColumns = {
                @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
        })
        *//*
    private Set<User> usersLiked = new HashSet<>();
    */


    // One product belongs to many orderItems, One order item points to one single product, OneToMany has to have the mappedBy, or problems otherwiese
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "article", cascade = CascadeType.REMOVE)
    private Set<Like> likes = new HashSet<>();


    private String permalink;

    private Integer views = 0;

    private ContentType contentType;

    @Transient
    private long commentCount;

    public Integer getViews() {
        return views == null ? 0 : views;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Collection<Comment> getComments() {
        return comments;
    }

    public void setComments(Collection<Comment> comments) {
        this.comments = comments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }


    public String getPermalink() {
        return permalink;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String getRenderedContent() {
        if (this.contentType == ContentType.MARKDOWN)
            return StringHelper.markdownToHtml(this.body);

        return body;
    }

    public void setPermalink(String permalink) {
        String token = permalink.toLowerCase().replace("\n", " ").replaceAll("[^a-z\\d\\s]", " ");
        this.permalink = StringUtils.arrayToDelimitedString(StringUtils.tokenizeToStringArray(token, " "), "-");
    }

    @Override
    public String getClassName() {
        return Article.class.getName();
    }

    @Override
    public Class getClassType() {
        return Article.class;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public ContentType getContentType() {
        return this.contentType;
    }

    public String getDescription() {
        return description;
    }

    public String getBody() {
        return body;
    }

    @PrePersist
    public void preCreate() {
        slugifyIfEmptySlug();
        if (publishOn == null || publishOn.isBefore(ZonedDateTime.now()))
            publishOn = ZonedDateTime.now();
    }

    @PreUpdate
    public void preUpdateEntity() {
        slugifyIfEmptySlug();
    }

    private void slugifyIfEmptySlug() {
        if (StringHelper.isEmpty(getSlug()))
            setSlug(StringHelper.slugify(getTitle()));

        if (publishOn == null || publishOn.isBefore(ZonedDateTime.now()))
            publishOn = ZonedDateTime.now();
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }


    public void setPublishOn(ZonedDateTime publishOn) {
        this.publishOn = publishOn;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentCount = commentsCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public ZonedDateTime getPublishOn() {
        return publishOn;
    }

    public List<ArticleImage> getImages() {
        return images;
    }

    public void setImages(List<ArticleImage> images) {
        this.images = images;
    }

    /*
    public Set<Like> getLikes() {
        return likes;
    }

    public void setLikes(Set<Like> likes) {
        this.likes = likes;
    }
    */
}
                