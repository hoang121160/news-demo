package com.example.demoweb.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class ArticleRequest {
    private String title;
    private String content;
    private String category;
    private List<MultipartFile> images;
}
