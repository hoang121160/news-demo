package com.example.demoweb.dto.response;


import java.time.LocalDateTime;
import java.util.List;

import com.example.demoweb.dto.ImageDTO;
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
