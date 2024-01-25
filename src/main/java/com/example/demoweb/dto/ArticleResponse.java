package com.example.demoweb.dto;


import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class ArticleResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime date;
    private String category;
    private List<ImageDTO> images;
}
