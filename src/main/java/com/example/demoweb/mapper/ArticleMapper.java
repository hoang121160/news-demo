package com.example.demoweb.mapper;

import com.example.demoweb.dto.response.ArticleAvatar;
import com.example.demoweb.dto.response.ArticleDetailView;
import com.example.demoweb.entity.Article;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper(componentModel = "spring")
@Service
public interface ArticleMapper {

    List<ArticleAvatar> toListDao(List<Article> articles);
    ArticleDetailView articleToArticleDetail(Article article);

}
