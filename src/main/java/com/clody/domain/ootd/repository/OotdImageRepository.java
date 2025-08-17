package com.clody.domain.ootd.repository;

import com.clody.domain.ootd.entity.OotdImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OotdImageRepository extends JpaRepository<OotdImage, Long> {
    @Query("SELECT oi.key FROM OotdImage oi WHERE oi.ootd.id = :ootdId")
    Optional<String> findKeyByOotdId(Long ootdId);

    @Query("""
           select oi.ootd.id as ootdId, oi.key as key
           from OotdImage oi
           where oi.ootd.id in :ootdIds
           """)
    List<ImageKeyView> findKeysByOotdIds(@Param("ootdIds") Collection<Long> ootdIds);

    interface ImageKeyView {
        Long getOotdId();
        String getKey();
    }
}
