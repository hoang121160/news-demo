package com.example.demoweb.repository;

import com.example.demoweb.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findArticleByCategory(String category, Pageable pageable);

}
