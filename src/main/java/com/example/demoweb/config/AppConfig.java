package com.example.demoweb.config;

import com.example.demoweb.dto.request.ArticleRequest;
import com.example.demoweb.dto.response.ArticleAvatar;
import com.example.demoweb.dto.response.ArticleDetailView;
import com.example.demoweb.entity.Article;
import com.example.demoweb.mapper.ArticleMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public ArticleMapper articleMapper() {
        return new ArticleMapper() {
            @Override
            public List<ArticleAvatar> toListDao(List<Article> articles) {
                return null;
            }

            @Override
            public ArticleDetailView articleToArticleDetail(Article article) {
                return null;
            }

            @Override
            public Article toEntity(ArticleRequest articleRequest) {
                return null;
            }
        };
    }
}
