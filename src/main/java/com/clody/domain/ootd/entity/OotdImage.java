package com.clody.domain.ootd.entity;

import com.clody.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "ootd_image")
public class OotdImage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "key", nullable = false)
    private String key;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ootd_id", nullable = false, unique = true) // 1:1 보장
    private Ootd ootd;

    public void setOotd(Ootd ootd) {
        this.ootd = ootd;
        if (ootd != null && ootd.getImage() != this) {
            ootd.setImage(this);
        }
    }
}
