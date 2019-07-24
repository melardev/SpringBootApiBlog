package com.melardev.spring.blogapi.dtos.response.articles;


import com.melardev.spring.blogapi.dtos.response.base.SuccessResponse;
import com.melardev.spring.blogapi.dtos.response.shared.PageMeta;
import com.melardev.spring.blogapi.entities.Article;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArticleListResponse extends SuccessResponse {


    private PageMeta pageMeta;
    private Collection<ArticleSummaryDto> articles;

    public ArticleListResponse(List<ArticleSummaryDto> articles, PageMeta pageMeta) {
        this.articles = articles;
        this.pageMeta = pageMeta;
    }

    public static ArticleListResponse build(Page<Article> articlesPage, String basePath) {
        List<ArticleSummaryDto> articleDtos = new ArrayList<>();
        List<Article> articles = articlesPage.getContent();
        for (Article article : articles) {
            articleDtos.add(ArticleSummaryDto.build(article));
        }
        return new ArticleListResponse(articleDtos, PageMeta.build(articlesPage, basePath));
    }


    public PageMeta getPageMeta() {
        return pageMeta;
    }

    public Collection<ArticleSummaryDto> getArticles() {
        return articles;
    }

    public void setArticles(Collection<ArticleSummaryDto> articles) {
        this.articles = articles;
    }
}
