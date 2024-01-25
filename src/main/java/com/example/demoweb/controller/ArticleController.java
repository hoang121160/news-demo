package com.example.demoweb.controller;

import com.example.demoweb.dto.ArticleRequest;
import com.example.demoweb.service.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }
    @Secured("ROLE_ADMIN")
    @PostMapping("/create")
    public ResponseEntity<String> createArticle(@RequestBody ArticleRequest articleRequest) {
        try {
            articleService.createArticle(articleRequest);
            return new ResponseEntity<>("Article created successfully", HttpStatus.CREATED);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Only ADMIN users can create articles", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error creating article", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
