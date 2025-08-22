package com.clody.domain.hashtag.service.query;

import com.clody.domain.hashtag.entity.Hashtag;
import com.clody.domain.hashtag.entity.HashtagCategory;

import java.util.Collection;
import java.util.List;

public interface HashtagQueryService {
    HashtagCategory parseCategoryOrThrow(String input);
    List<HashtagCategory> parseCategoryListOrThrow(List<String> raw);
    List<Hashtag> findAllByCategoriesOrThrow(Collection<HashtagCategory> categories);

}
