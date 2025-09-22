package com.clody.domain.ootd.service.ootdQueryService;

import com.clody.domain.member.entity.Member;
import com.clody.domain.ootd.dto.OotdResponseDTO;

import java.util.List;

public interface OotdQueryService {
    OotdResponseDTO.getMonthlyOotdListDTO getMonthlyOotds(int year, int month,long memberId);
    OotdResponseDTO.getCommunityOotdListDTO getCommunityOotds(Long cursor, int size, List<String> koreanTags);
    OotdResponseDTO.getOotdDTO getOotd(Long ootdId);
    OotdResponseDTO.getSimilarOotdListDTO getRandomSimilarOotds(int minTemp, int maxTemp, boolean rain,long memberId);
}
