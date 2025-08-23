package com.clody.domain.ootd.entity;

import com.clody.domain.member.entity.Member;
import com.clody.global.entity.BaseOnlyCreateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "ootd_like",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_ootd_like_ootd_member",
                columnNames = {"ootd_id", "member_id"}
        ),
        indexes = {
                @Index(name = "idx_like_ootd", columnList = "ootd_id"),
                @Index(name = "idx_like_member", columnList = "member_id")
        }
)
public class OotdLike extends BaseOnlyCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ootd_id")
    private Ootd ootd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
