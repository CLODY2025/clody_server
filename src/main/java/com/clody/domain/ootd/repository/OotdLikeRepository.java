package com.clody.domain.ootd.repository;

import com.clody.domain.ootd.entity.OotdLike;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OotdLikeRepository extends JpaRepository<OotdLike, Long> {

    Optional<OotdLike> findByOotd_IdAndMember_Id(Long ootdId, Long memberId);

    boolean existsByOotd_IdAndMember_Id(Long ootdId, Long memberId);

    long countByOotd_Id(Long ootdId);

    @Modifying
    @Query("delete from OotdLike l where l.ootd.id = :ootdId and l.member.id = :memberId")
    void deleteByOotdIdAndMemberId(@Param("ootdId") Long ootdId, @Param("memberId") Long memberId);
}