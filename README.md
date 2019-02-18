# Introduction
This is a Spring Boot Blog Api application, it is not finished, but I have written some code already.
# TODO
- Rethink findByUsernameOrEmail issue, user impersonation possible?
- Order HomeResponse, first Articles, then tags then categories
- Annotation for authenticated users for actions in LikeController and SubscriptionsController
- Enforce Article slug uniqueness
- Implement Paging on Subscriptions Dto
- Alias URLs for example /articles/slug/likes /likes/article_slug should be the same
- Get Articles Not Liked by user
- Get Users have not liked an article
- Delete PageMeta from AppResponse
- Like is allowed to be saved with article_id = null, fix it
- File Upload
- Saving Like Objects with Article and/or User Set does not work, saving them with UserId and ArticleId does....
- Article Featured Image
- Implement tags:articles_count in ArticleDataSection.java:
    - Create a articles_count column in tags
    - Pass the already precomputed count from HomeResponse to ArticleSummaryDto.java
    
    
