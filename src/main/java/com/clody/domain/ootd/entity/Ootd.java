package com.clody.domain.ootd.entity;

import com.clody.domain.comment.entity.Comment;
import com.clody.domain.member.entity.Member;
import com.clody.domain.ootdHashtag.entity.OotdHashtag;
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

    @Column(name = "is_rain")
    private boolean isRain;

    @Column(name = "humidity")
    private int humidity;

    @Column(name = "weather_description")
    private String weatherDescription;


    @OneToOne(mappedBy = "ootd", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private OotdImage image;

    public void setImage(OotdImage image) {
        this.image = image;
        if (image != null && image.getOotd() != this) {
            image.setOotd(this);
        }
    }

    @Builder.Default
    @OneToMany(mappedBy = "ootd", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "ootd", cascade = CascadeType.ALL)
    private List<OotdLike> likes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "ootd", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OotdHashtag> ootdHashtags = new ArrayList<>();
}
