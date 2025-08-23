package com.clody.domain.comment.repository;

import com.clody.domain.comment.entity.CommentClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentClosureRepository extends JpaRepository<CommentClosure, Long> {

    @Modifying
    @Transactional
    @Query(value = "insert into comment_closure(ancestor_id, descendant_id, depth) values (:newId, :newId, 0)", nativeQuery = true)
    void insertSelfLink(@Param("newId") Long newId);

    @Modifying @Transactional
    @Query(value = """
        insert into comment_closure(ancestor_id, descendant_id, depth)
        select cc.ancestor_id, :newId, cc.depth + 1
        from comment_closure cc
        where cc.descendant_id = :parentId
    """, nativeQuery = true)
    void insertAncestorLinksFromParent(@Param("parentId") Long parentId, @Param("newId") Long newId);

    @Query(value = "select descendant_id from comment_closure where ancestor_id = :commentId", nativeQuery = true)
    List<Long> findSubtreeIds(@Param("commentId") Long commentId);

    @Modifying @Transactional
    @Query(value = "delete from comment_closure where ancestor_id in (:ids) or descendant_id in (:ids)", nativeQuery = true)
    void deleteLinksForIds(@Param("ids") List<Long> ids);
}
