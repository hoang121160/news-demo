package com.example.demoweb.dto.response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleAvatar extends BaseDto {
    private Long id;
    private String title;
    private String contentSnippet;
    private String image;
}
