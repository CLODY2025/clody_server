package com.clody.domain.ootd.service.ootdQueryService;


import com.clody.domain.hashtag.entity.HashtagCategory;
import com.clody.domain.hashtag.exception.HashtagErrorCode;
import com.clody.domain.hashtag.exception.HashtagException;
import com.clody.domain.hashtag.service.query.HashtagQueryService;
import com.clody.domain.member.entity.Member;
import com.clody.domain.member.repository.MemberRepository;
import com.clody.domain.ootd.dto.OotdResponseDTO;
import com.clody.domain.ootd.entity.Ootd;
import com.clody.domain.ootd.exception.OotdErrorCode;
import com.clody.domain.ootd.exception.OotdException;
import com.clody.domain.ootd.repository.OotdImageRepository;
import com.clody.domain.ootd.repository.OotdRepository;
import com.clody.domain.ootdHashtag.repository.OotdHashtagRepository;
import com.clody.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OotdQueryServiceImpl implements OotdQueryService {
    private final OotdRepository ootdRepository;
    private final OotdImageRepository ootdImageRepository;
    private final OotdHashtagRepository ootdHashtagRepository;
    private final HashtagQueryService hashtagQueryService;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;


    /* ootd 둘러보기(전체 공개) */
    public OotdResponseDTO.getCommunityOotdListDTO getCommunityOotds(Long cursor, int size, List<String> koreanTags) {

        List<HashtagCategory> categories =
                (koreanTags == null || koreanTags.isEmpty())
                        ? List.of()
                        : hashtagQueryService.parseCategoryListOrThrow(koreanTags);

        Pageable pageable = PageRequest.of(0, size + 1);
        List<Ootd> page;
        if (categories.isEmpty()) {
            page = (cursor == null)
                    ? ootdRepository.findFirstPageAll(pageable)
                    : ootdRepository.findNextPageAll(cursor, pageable);
        } else {
            page = (cursor == null)
                    ? ootdRepository.findFirstPageByCategories(categories, pageable)
                    : ootdRepository.findNextPageByCategories(categories, cursor, pageable);
        }

        boolean hasNext = page.size() > size;
        if (hasNext) page = page.subList(0, size);

        if (page.isEmpty()) {
            return OotdResponseDTO.getCommunityOotdListDTO.builder()
                    .items(List.of())
                    .nextCursor(null)
                    .hasNext(false)
                    .build();
        }

        List<Long> ootdIds = page.stream().map(Ootd::getId).toList();


        Map<Long, String> imageKeyMap = ootdImageRepository.findKeysByOotdIds(ootdIds).stream()
                .collect(Collectors.toMap(OotdImageRepository.ImageKeyView::getOotdId,
                        OotdImageRepository.ImageKeyView::getKey));

        Map<Long, String> presignedUrlMap = new HashMap<>();
        for (Long id : ootdIds) {
            String key = imageKeyMap.get(id);
            if (key != null && !key.isBlank()) {
                String url = s3Service.getGetGeneratePresignedUrlRequest(key); // 프로젝트 시그니처에 맞춤
                presignedUrlMap.put(id, url);
            }
        }

        Map<Long, List<String>> hashtagMap = new HashMap<>();
        for (OotdHashtagRepository.OotdHashtagCategoryView v
                : ootdHashtagRepository.findCategoriesByOotdIds(ootdIds)) {
            hashtagMap.computeIfAbsent(v.getOotdId(), k -> new ArrayList<>())
                    .add(v.getCategory().getKoreanName());
        }

        List<OotdResponseDTO.getOotdDTO> items = page.stream()
                .map(o -> OotdResponseDTO.getOotdDTO.builder()
                        .id(o.getId())
                        .nickname(o.getMember().getNickname())
                        .image(presignedUrlMap.get(o.getId()))
                        .hashtags(hashtagMap.getOrDefault(o.getId(), List.of()))
                        .createdAt(o.getCreatedAt())
                        .build())
                .toList();

        Long nextCursor = hasNext ? items.get(items.size() - 1).getId() : null;

        return OotdResponseDTO.getCommunityOotdListDTO.builder()
                .items(items)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }

    /* 월별 ootd list 조회(개인용, 팔로워용) */
    @Override
    public OotdResponseDTO.getMonthlyOotdListDTO getMonthlyOotds(int year, int month) {
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endOfMonth = startOfMonth
                .withDayOfMonth(startOfMonth.toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999_000_000);

        Member member = memberRepository.findById(1L).orElseThrow(() -> new OotdException(OotdErrorCode.MEMBER_NOT_FOUND));

        List<Ootd> ootdList = ootdRepository.findByMemberIdAndCreatedAtBetween(member.getId(), startOfMonth, endOfMonth);
        List<OotdResponseDTO.getMonthlyOotdDTO> items = ootdList.stream()
                .map(o -> OotdResponseDTO.getMonthlyOotdDTO.builder()
                        .id(o.getId())
                        .date(o.getCreatedAt())
                        .build())
                .toList();
        return OotdResponseDTO.getMonthlyOotdListDTO.builder()
                .ootds(items)
                .build();
    }

    /* 비슷한 ootd 조회 */
    @Override
    public OotdResponseDTO.getSimilarOotdListDTO getRandomSimilarOotds(
            int minTemp, int maxTemp, boolean rain
    ) {
        //멤버 하드코딩
        List<OotdResponseDTO.getSimilarOotdDTO> items = ootdRepository
                .findRandomSimilarByMemberWithImageAndTags(1L, minTemp, maxTemp, rain)
                .stream()
                .map(row -> {
                    Long id = ((Number) row[0]).longValue();
                    String imageKey = (String) row[1];

                    List<String> hashtags = extractHashtags(row[2]);

                    String imageUrl = (imageKey != null && !imageKey.isBlank())
                            ? s3Service.getGetGeneratePresignedUrlRequest(imageKey)
                            : null;

                    return OotdResponseDTO.getSimilarOotdDTO.builder()
                            .id(id)
                            .image(imageUrl)
                            .hashtags(hashtags)
                            .build();
                })
                .toList();

        return OotdResponseDTO.getSimilarOotdListDTO.builder()
                .ootds(items)
                .build();
    }

    private List<String> extractHashtags(Object raw) {
        if (raw instanceof java.sql.Array sqlArray) {
            try {
                return Arrays.asList((String[]) sqlArray.getArray());
            } catch (SQLException e) {
                throw new HashtagException(HashtagErrorCode.RETURN_FAIL);
            }
        }
        return Arrays.asList((String[]) raw);
    }


    /* ootd 개별 조회시 사용 */
    @Override
    @Transactional(readOnly = true)
    public OotdResponseDTO.getOotdDTO getOotd(Long ootdId) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow(() -> new OotdException(OotdErrorCode.NOT_FOUND));
        List<HashtagCategory> categories = ootdHashtagRepository.findCategoriesByOotdId(ootdId);
        List<String> hashtagNames = categories.stream()
                .map(HashtagCategory::getKoreanName)
                .toList();
        String key=ootdImageRepository.findKeyByOotdId(ootdId).orElseThrow(() -> new OotdException(OotdErrorCode.NOT_FOUND));
        String presingedUrl=s3Service.getGetGeneratePresignedUrlRequest(key);
        return OotdResponseDTO.getOotdDTO.builder()
                .id(ootd.getId())
                .nickname(ootd.getMember().getNickname())
                .hashtags(hashtagNames)
                .createdAt(ootd.getCreatedAt())
                .image(presingedUrl)
                .build();
    }





}
