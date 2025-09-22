package com.clody.domain.ootd.service.ootdCommandService;


import com.clody.domain.hashtag.entity.Hashtag;
import com.clody.domain.hashtag.entity.HashtagCategory;
import com.clody.domain.hashtag.exception.HashtagErrorCode;
import com.clody.domain.hashtag.exception.HashtagException;
import com.clody.domain.hashtag.repository.HashtagRepository;
import com.clody.domain.hashtag.service.query.HashtagQueryService;
import com.clody.domain.member.entity.Member;
import com.clody.domain.member.repository.MemberRepository;
import com.clody.domain.ootd.dto.OotdRequestDTO;
import com.clody.domain.ootd.dto.OotdResponseDTO;
import com.clody.domain.ootd.entity.Ootd;
import com.clody.domain.ootd.entity.OotdImage;
import com.clody.domain.ootd.exception.OotdErrorCode;
import com.clody.domain.ootd.exception.OotdException;
import com.clody.domain.ootd.repository.OotdRepository;
import com.clody.domain.ootdHashtag.entity.OotdHashtag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class OotdCommandServiceImpl implements OotdCommandService {

    private final OotdRepository ootdRepository;
    private final HashtagRepository hashtagRepository;
    private final HashtagQueryService hashtagQueryService;
    private final MemberRepository memberRepository;

    @Override
    public OotdResponseDTO.getOotdDTO createOotd(OotdRequestDTO.CreateOotdDTO request,Member member) {
        Member currentMember = memberRepository.findById(member.getId()).orElseThrow(() -> new OotdException(OotdErrorCode.MEMBER_NOT_FOUND));

        List<String> rawTags = Optional.ofNullable(request.getHashtags()).orElseGet(List::of);
        if (rawTags.size() > 6) {
            throw new HashtagException(HashtagErrorCode.TOO_MANY_HASHTAGS);
        }
        List<HashtagCategory> categories = hashtagQueryService.parseCategoryListOrThrow(rawTags);
        List<Hashtag> hashtags = hashtagQueryService.findAllByCategoriesOrThrow(new HashSet<>(categories));

        Ootd ootd = Ootd.builder()
                .member(currentMember)
                .minTemperature(request.getMinTemperature())
                .maxTemperature(request.getMaxTemperature())
                .weatherDescription(request.getWeatherDescription())
                .isRain(request.getRain())
                .humidity(request.getHumidity())
                .build();

        if (request.getKey() != null && !request.getKey().isBlank()) {
            OotdImage image = OotdImage.builder()
                    .key(request.getKey())
                    .ootd(ootd)
                    .build();
            ootd.setImage(image);
        }

        for (Hashtag tag : hashtags) {
            OotdHashtag mapping = OotdHashtag.builder()
                    .ootd(ootd)
                    .hashtag(tag)
                    .build();
            ootd.getOotdHashtags().add(mapping);
        }
        Ootd saved = ootdRepository.save(ootd);
        List<String> hashtagDisplays = saved.getOotdHashtags().stream()
                .map(oh -> oh.getHashtag().getCategory().getKoreanName())
                .toList();

        return OotdResponseDTO.getOotdDTO.builder()
                .id(saved.getId())
                .nickname(saved.getMember().getNickname())
                .image(saved.getImage().getKey())
                .hashtags(hashtagDisplays)
                .createdAt(saved.getCreatedAt())
                .build();

    }




}
