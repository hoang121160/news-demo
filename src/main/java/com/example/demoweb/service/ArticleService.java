package com.example.demoweb.service;

import com.example.demoweb.dto.ArticleRequest;
import com.example.demoweb.entity.Article;
import com.example.demoweb.entity.Image;
import com.example.demoweb.entity.User;
import com.example.demoweb.repository.ArticleRepository;
import com.example.demoweb.repository.CommentRepository;
import com.example.demoweb.repository.ImageRepository;
import com.example.demoweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleService {
    private final AuthenticationManager authenticationManager;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    public ArticleService(UserRepository userRepository,
                          ArticleRepository articleRepository,
                          AuthenticationManager authenticationManager,
                          ImageRepository imageRepository){
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.authenticationManager = authenticationManager;
        this.imageRepository = imageRepository;
    }

    public void createArticle(ArticleRequest articleRequest){
        try {
            if (!isAdmin()){
                throw new BadCredentialsException("Only ADMIN users can create articles");
            }
                User currentUser = getCurrentUser();
            if (currentUser == null) {
                throw new BadCredentialsException("User not authenticated");
            }
            Article article = convertToEntity(articleRequest, currentUser);
            articleRepository.save(article);
            handleImages(article, articleRequest.getImages());
        }catch (RuntimeException  e){
            e.printStackTrace();
        }
    }
    private boolean isAdmin() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            return userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            return userRepository.findByUsername(username);
        }
        return null;
    }
    private Article convertToEntity(ArticleRequest articleRequest, User currentUser) {
        Article article = new Article();
        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());
        article.setCategory(articleRequest.getCategory());
        article.setDate(LocalDateTime.now());
        article.setAuthor(currentUser);

        List<Image> images = convertToImages(articleRequest.getImages());
        article.setImages(images);
        return article;
    }

    private List<Image> convertToImages(List<MultipartFile> imageFiles) {
        List<Image> images = new ArrayList<>();
        for (MultipartFile imageFile  : imageFiles){
            try {
                Image image = new Image();
                image.setFilename(imageFile.getOriginalFilename());
                image.setData(imageFile.getBytes());
                images.add(image);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return images;
    }

    private void handleImages(Article article, List<MultipartFile> imageFiles) {
        for (MultipartFile imageFile : imageFiles) {
            try {
                // Xử lý và lưu hình ảnh vào cơ sở dữ liệu
                Image image = new Image();
                image.setFilename(imageFile.getOriginalFilename());
                image.setData(imageFile.getBytes());
                image.setArticle(article);
                imageRepository.save(image);
                article.addImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
