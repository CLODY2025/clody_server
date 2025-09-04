package com.clody.domain.ootd.service.ootdLikeQueryService;

import com.clody.domain.ootd.dto.OotdLikeResponseDTO;

public interface OotdLikeQueryService {
    OotdLikeResponseDTO.StateDTO state(Long ootdId, Long memberId);
    OotdLikeResponseDTO.CountDTO count(Long ootdId);
}
