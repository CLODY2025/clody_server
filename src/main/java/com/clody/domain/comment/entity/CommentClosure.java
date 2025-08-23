package com.clody.domain.comment.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "comment_closure",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ancestor_id","descendant_id"}),
        indexes = {
                @Index(name = "idx_cc_ancestor_depth", columnList = "ancestor_id, depth"),
                @Index(name = "idx_cc_descendant", columnList = "descendant_id")
        })
public class CommentClosure {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "ancestor_id", nullable = false)
    private Comment ancestor;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "descendant_id", nullable = false)
    private Comment descendant;

    @Column(nullable = false)
    private int depth;
}
