package com.example.demoweb.service;

import com.example.demoweb.dto.CommentDTO;
import com.example.demoweb.entity.Article;
import com.example.demoweb.entity.Comment;
import com.example.demoweb.entity.User;
import com.example.demoweb.exception.UserNotFoundException;
import com.example.demoweb.repository.ArticleRepository;
import com.example.demoweb.repository.CommentRepository;
import com.example.demoweb.repository.UserRepository;
import com.github.dockerjava.api.exception.UnauthorizedException;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.AuthenticationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
public class CommentService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    public CommentService(CommentRepository commentRepository,
                          UserRepository userRepository,
                          ArticleRepository articleRepository,
                          AuthenticationManager authenticationManager){
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.authenticationManager = authenticationManager;
    }
    public List<Comment> getAllComment(){
       return commentRepository.findAll();
    }
//    public List<CommentDTO> getCommentsForArticle(Long articleId){
//        List<Comment> comments = commentRepository.findByArticleId(articleId);
//        List<CommentDTO> commentDTOs = new ArrayList<>();
//        for (Comment comment : comments){
//            CommentDTO commentDTO = new CommentDTO();
//            commentDTO.setUsername(comment.getUsername());
//            commentDTO.setContent(commentDTO.getContent());
//            commentDTOs.add(commentDTO);
//        }
//        return commentDTOs;
//    }
    public CommentDTO createComment(String content, Long articleId) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            if (authenticatedUser == null) {
                throw new BadCredentialsException("User not authenticated");
            }
            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new UserNotFoundException("Article not found with ID: " + articleId));
                Comment comment = new Comment();
                comment.setContent(content);
                comment.setDate(LocalDateTime.now());
                comment.setArticle(article);
                comment.setUser(authenticatedUser);

                Comment createdComment = commentRepository.save(comment);

                // Tạo CommentDTO và trả về
                CommentDTO commentDTO = new CommentDTO();
                commentDTO.setUsername(createdComment.getUser().getUsername());
                commentDTO.setContent(createdComment.getContent());
                commentDTO.setDate(createdComment.getDate());

                return commentDTO;
        } catch (BadCredentialsException e) {
            throw e;
        }
    }


    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
    public void deleteComment(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new UserNotFoundException("Comment not found with ID: " + commentId));
        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser == null) {
            throw new UnauthorizedException("User not authorized to delete this comment");
        }
        if (!authenticatedUser.getId().equals(comment.getUser().getId())) {
            throw new UnauthorizedException("User not authorized to delete this comment");
        }
            Article article = comment.getArticle();
            article.getComments().remove(comment);
            articleRepository.save(article);

            User user = comment.getUser();
            user.getComments().remove(comment);
            userRepository.save(user);
            commentRepository.delete(comment);
    }

}
