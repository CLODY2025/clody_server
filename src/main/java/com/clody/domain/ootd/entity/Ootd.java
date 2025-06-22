package com.clody.domain.ootd.entity;

import com.clody.domain.comment.entity.Comment;
import com.clody.domain.member.entity.Member;
import com.clody.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "ootd")
public class Ootd extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "min_temperature")
    private int minTemperature;

    @Column(name = "max_temperature")
    private int maxTemperature;

    @Column(name = "weather_description")
    private String weatherDescription;

    @Column(name = "is_public")
    private boolean isPublic;

    @Builder.Default
    @OneToMany(mappedBy = "ootd", cascade = CascadeType.ALL)
    private List<OotdImage> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "ootd", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "ootd", cascade = CascadeType.ALL)
    private List<OotdLike> likes = new ArrayList<>();
}
