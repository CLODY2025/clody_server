package com.clody.domain.hashtag.service.query;

import com.clody.domain.hashtag.entity.Hashtag;
import com.clody.domain.hashtag.entity.HashtagCategory;
import com.clody.domain.hashtag.exception.HashtagErrorCode;
import com.clody.domain.hashtag.exception.HashtagException;
import com.clody.domain.hashtag.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashtagQueryServiceImpl implements HashtagQueryService {
    private final HashtagRepository hashtagRepository;

    @Override
    public HashtagCategory parseCategoryOrThrow(String input) {
        if (input == null || input.isBlank()) {
            throw new HashtagException(HashtagErrorCode.BLANK_EXIST);
        }
        String trimmed = input.trim();

        for (HashtagCategory c : HashtagCategory.values()) {
            if (c.getKoreanName().equals(trimmed)) {
                return c;
            }
        }
        log.error("지원하지 않는 해시태그"+input);
        throw new HashtagException(HashtagErrorCode.NOT_FOUND);

    }
    @Override
    public List<HashtagCategory> parseCategoryListOrThrow(List<String> raw) {
        if (raw == null) return List.of();
        return raw.stream().map(this::parseCategoryOrThrow).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hashtag> findAllByCategoriesOrThrow(Collection<HashtagCategory> categories) {
        if (categories == null || categories.isEmpty()) return List.of();
        List<Hashtag> found = hashtagRepository.findByCategoryIn(categories);

        if (found.size() != new HashSet<>(categories).size()) {
            Set<HashtagCategory> foundSet = found.stream()
                    .map(Hashtag::getCategory)
                    .collect(Collectors.toSet());
            List<HashtagCategory> missing = categories.stream()
                    .filter(c -> !foundSet.contains(c))
                    .toList();
            log.error("존재하지 않는 해시태그"+missing);
            throw new HashtagException(HashtagErrorCode.NOT_FOUND);
        }
        return found;
    }
}
