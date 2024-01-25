package com.example.demoweb.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class ArticleAvatar {
    private Long id;
    private String title;
    private String contentSnippet;
    private String imageUrl;
}
