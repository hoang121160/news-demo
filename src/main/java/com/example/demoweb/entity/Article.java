package com.example.demoweb.entity;
import javax.persistence.*;
//import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(name = "title")
    private String title;

    @NotBlank(message = "Content is required")
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @NotNull(message = "Date is required")
    @Column(name = "date")
    private LocalDateTime date;

    @NotBlank(message = "Category is required")
    @Column(name = "category")
    private String category;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "article_id")
    private List<Image> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(mappedBy = "article")
    private List<Comment> comments = new ArrayList<>();

    public void addImage(Image image) {
        images.add(image);
        image.setArticle(this);
    }

}
