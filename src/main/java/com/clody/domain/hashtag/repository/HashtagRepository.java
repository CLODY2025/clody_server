package com.clody.domain.hashtag.repository;

import com.clody.domain.hashtag.entity.Hashtag;
import com.clody.domain.hashtag.entity.HashtagCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    List<Hashtag> findByCategoryIn(Collection<HashtagCategory> categories);
}
