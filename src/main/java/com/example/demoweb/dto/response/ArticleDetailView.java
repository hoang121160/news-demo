package com.example.demoweb.dto.response;

import com.example.demoweb.dto.CommentDTO;
import com.example.demoweb.dto.ImageDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDetailView {
    private String title;
    private String content;
    private LocalDateTime date;
    private String category;
    private List<ImageDTO> images;
    private List<CommentDTO> comments;
}
