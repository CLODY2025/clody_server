package com.clody.domain.comment.repository;

import com.clody.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
      select c from Comment c
      join fetch c.member m
      where c.ootd.id = :ootdId
      order by c.createdAt asc, c.id asc
    """)
    List<Comment> findAllByOotdIdWithMember(@Param("ootdId") Long ootdId);

    @Query("select count(c) from Comment c where c.ootd.id = :ootdId")
    long countByOotdId(@Param("ootdId") Long ootdId);
}
