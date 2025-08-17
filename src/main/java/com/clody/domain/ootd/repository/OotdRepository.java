package com.clody.domain.ootd.repository;


import com.clody.domain.hashtag.entity.HashtagCategory;
import com.clody.domain.ootd.entity.Ootd;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OotdRepository extends JpaRepository<Ootd, Long> {
    @Query("SELECT o FROM Ootd o WHERE o.member.id = :memberId AND o.createdAt BETWEEN :startOfMonth AND :endOfMonth")
    List<Ootd> findByMemberIdAndCreatedAtBetween(@Param("memberId") Long memberId,
                                                 @Param("startOfMonth") LocalDateTime startOfMonth,
                                                 @Param("endOfMonth") LocalDateTime endOfMonth);

    @Query(value = """
        SELECT o.id AS id,
               oi.key AS image_key,
               ARRAY_AGG(h.category) AS hashtags
        FROM ootd o
        JOIN ootd_image oi ON oi.ootd_id = o.id
        JOIN ootd_hashtag oh ON oh.ootd_id = o.id
        JOIN hashtag h ON h.id = oh.hashtag_id
        WHERE o.member_id = :memberId
          AND ((o.min_temperature + o.max_temperature) / 2) BETWEEN :minTemp AND :maxTemp
          AND o.is_rain = :rain
        GROUP BY o.id, oi.key
        ORDER BY RANDOM()
        LIMIT 4
        """, nativeQuery = true)
    List<Object[]> findRandomSimilarByMemberWithImageAndTags(
            @Param("memberId") Long memberId,
            @Param("minTemp") int minTemp,
            @Param("maxTemp") int maxTemp,
            @Param("rain") boolean rain
    );

    @Query("select o from Ootd o order by o.id desc")
    List<Ootd> findFirstPageAll(Pageable pageable);

    @Query("select o from Ootd o where o.id < :cursor order by o.id desc")
    List<Ootd> findNextPageAll(@Param("cursor") Long cursor, Pageable pageable);

    @Query("""
           select o from Ootd o
           where exists (
              select 1 from OotdHashtag oh join oh.hashtag h
              where oh.ootd = o and h.category in :categories
           )
           order by o.id desc
           """)
    List<Ootd> findFirstPageByCategories(@Param("categories") Collection<HashtagCategory> categories,
                                         Pageable pageable);

    @Query("""
           select o from Ootd o
           where o.id < :cursor
             and exists (
                select 1 from OotdHashtag oh join oh.hashtag h
                where oh.ootd = o and h.category in :categories
             )
           order by o.id desc
           """)
    List<Ootd> findNextPageByCategories(@Param("categories") Collection<HashtagCategory> categories,
                                        @Param("cursor") Long cursor,
                                        Pageable pageable);

}
