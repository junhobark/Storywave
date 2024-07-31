package com.example.storywave.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "board")
@JsonIgnoreProperties({"posts", "categories"}) // 순환 참조 방지
public class Board {

    @Id
    private Long postTypeId;
    private Integer viewPost;

    @OneToMany(mappedBy = "board")
    private Set<Post> posts;

    @OneToMany(mappedBy = "board")
    private Set<Category> categories;
}