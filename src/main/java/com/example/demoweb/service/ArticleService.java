package com.example.demoweb.service;

import com.example.demoweb.dto.request.ApiListBaseRequest;
import com.example.demoweb.dto.request.ArticleRequest;
import com.example.demoweb.dto.response.ArticleAvatar;
import com.example.demoweb.dto.response.ArticleDetailView;
import com.example.demoweb.dto.response.BasePage;
import com.example.demoweb.entity.Article;
import com.example.demoweb.entity.Image;
import com.example.demoweb.entity.User;
import com.example.demoweb.exception.MasterException;
import com.example.demoweb.mapper.ArticleMapper;
import com.example.demoweb.repository.ArticleRepository;
import com.example.demoweb.repository.ImageRepository;
import com.example.demoweb.repository.UserRepository;
import com.example.demoweb.utils.FilterDataUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ArticleService {
    private final AuthenticationManager authenticationManager;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final ArticleMapper mapper;

    public ArticleService(UserRepository userRepository,
                          ArticleRepository articleRepository,
                          AuthenticationManager authenticationManager,
                          ImageRepository imageRepository, ArticleMapper mapper) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.authenticationManager = authenticationManager;
        this.imageRepository = imageRepository;
        this.mapper = mapper;
    }

    public void createArticle(ArticleRequest articleRequest) {
        try {
            if (!isAdmin()) {
                throw new BadCredentialsException("Only ADMIN users can create articles");
            }
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                throw new BadCredentialsException("User not authenticated");
            }
            Article article = convertToEntity(articleRequest, currentUser);
            articleRepository.save(article);
            handleImages(article, articleRequest.getImages());
        } catch (RuntimeException e) {
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
        for (MultipartFile imageFile : imageFiles) {
            try {
                Image image = new Image();
                image.setFilename(imageFile.getOriginalFilename());
                image.setData(imageFile.getBytes());
                images.add(image);
            } catch (IOException e) {
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
    protected BasePage<ArticleAvatar> map(Page<Article> page) {
        BasePage<ArticleAvatar> rPage = new BasePage<>();
        rPage.setData(mapper.toListDao(page.getContent()));
        rPage.setTotalPage(page.getTotalPages());
        rPage.setTotalRecord( page.getTotalElements());
        rPage.setPage(page.getPageable().getPageNumber());
        return rPage;
    }

    public BasePage<ArticleAvatar> getArticlesByCategory(ApiListBaseRequest apiListBaseRequest, String category) {
        Pageable pageable = FilterDataUtil.buildPageRequest(apiListBaseRequest);
        Page<Article> page = articleRepository.findArticleByCategory(category, pageable);
        return this.map(page);
    }
    public ArticleDetailView getArticleById(Long id){
        Article article = articleRepository.findById(id).orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Hừm...trang này không tồn tại. Hãy thử tìm kiếm nội dung khác."));
        return mapper.articleToArticleDetail(article);
    }
    public void deleteArticle(Long id){
        try {
            if(isAdmin()) {
                Objects.requireNonNull(id, "ID của Article không được null");
                Article article = articleRepository.findById(id).orElseThrow(()-> new MasterException(HttpStatus.NOT_FOUND, "Không tìm thấy Bài viết với ID"));
                articleRepository.delete(article);
            }
        } catch (RuntimeException e){
            e.printStackTrace();
        }
    }


}
