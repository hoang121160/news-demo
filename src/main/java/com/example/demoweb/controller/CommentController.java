package com.example.demoweb.controller;

import com.example.demoweb.dto.CommentDTO;
import com.example.demoweb.entity.Comment;
import com.example.demoweb.entity.User;
import com.example.demoweb.service.CommentService;
import com.github.dockerjava.api.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private CommentService commentService;


    @PostMapping("/create-comment")
    public ResponseEntity<CommentDTO> createComment(@RequestParam String content, @RequestParam Long articleId) {
        try {
            Comment createdComment = commentService.createComment(content, articleId);

            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setUsername(createdComment.getUser().getUsername());
            commentDTO.setContent(createdComment.getContent());
            commentDTO.setDate(createdComment.getDate());

            return ResponseEntity.ok(commentDTO);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok("Comment deleted successfully");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
