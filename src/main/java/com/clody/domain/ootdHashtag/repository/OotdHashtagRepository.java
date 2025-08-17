package com.clody.domain.ootdHashtag.repository;

import com.clody.domain.hashtag.entity.HashtagCategory;
import com.clody.domain.ootd.entity.Ootd;
import com.clody.domain.ootdHashtag.dto.OotdHashtagResponseDTO;
import com.clody.domain.ootdHashtag.entity.OotdHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OotdHashtagRepository extends JpaRepository<OotdHashtag, Long> {

    @Query("select h.category " +
            "from OotdHashtag oh join oh.hashtag h " +
            "where oh.ootd.id = :ootdId")
    List<HashtagCategory> findCategoriesByOotdId(@Param("ootdId") Long ootdId);

    @Query("""
           select oh.ootd.id as ootdId, h.category as category
           from OotdHashtag oh join oh.hashtag h
           where oh.ootd.id in :ootdIds
           """)
    List<OotdHashtagCategoryView> findCategoriesByOotdIds(@Param("ootdIds") Collection<Long> ootdIds);

    interface OotdHashtagCategoryView {
        Long getOotdId();
        HashtagCategory getCategory();
    }
}
