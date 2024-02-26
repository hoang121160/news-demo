package com.example.demoweb;

import com.example.demoweb.dto.request.ArticleRequest;
import com.example.demoweb.entity.Article;
import com.example.demoweb.repository.ArticleRepository;
import com.example.demoweb.repository.ImageRepository;
import com.example.demoweb.service.ArticleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ArticleService articleService;

    @Test
    public void testCreateArticle() {
        ArticleRequest articleRequest = new ArticleRequest();
        // Set articleRequest properties

        Article article = new Article();
        // Set article properties based on articleRequest

        MultipartFile imageFile = new MockMultipartFile("image", "image.jpg", "image/jpeg", "some image".getBytes());
        List<MultipartFile> imageFiles = Collections.singletonList(imageFile);

        when(articleRepository.save(any(Article.class))).thenReturn(article);

        articleService.createArticle(articleRequest);

        // Verify that the handleImages method is called with the correct arguments
        verify(articleService, times(1)).handleImages(eq(article), eq(imageFiles));
    }
}
