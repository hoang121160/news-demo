package com.example.demoweb.mapper;

import com.example.demoweb.dto.request.ArticleRequest;
import com.example.demoweb.dto.response.ArticleAvatar;
import com.example.demoweb.dto.response.ArticleDetailView;
import com.example.demoweb.dto.response.ArticleResponse;
import com.example.demoweb.entity.Article;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;


@Mapper(componentModel = "spring")
@Service
@Component
public interface ArticleMapper {

    List<ArticleAvatar> toListDao(List<Article> articles);
    ArticleDetailView articleToArticleDetail(Article article);
    Article toEntity(ArticleRequest articleRequest);
}
