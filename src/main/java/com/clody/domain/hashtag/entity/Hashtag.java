package com.clody.domain.hashtag.entity;

import com.clody.domain.ootdHashtag.entity.OotdHashtag;
import jakarta.persistence.*;
import lombok.*;
import com.clody.global.entity.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "hashtag")
public class Hashtag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", columnDefinition = "VARCHAR(20)")
    private HashtagCategory category;

    @Column(name = "image_key", nullable = false)
    private String imageKey;

    @Builder.Default
    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.ALL)
    private List<OotdHashtag> ootdHashtags = new ArrayList<>();
}
