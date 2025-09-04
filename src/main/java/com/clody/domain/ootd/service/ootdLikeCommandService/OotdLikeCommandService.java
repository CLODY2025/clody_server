package com.clody.domain.ootd.service.ootdLikeCommandService;

import com.clody.domain.ootd.dto.OotdLikeResponseDTO;

public interface OotdLikeCommandService {
    OotdLikeResponseDTO.StateDTO like(Long ootdId, Long memberId);
    OotdLikeResponseDTO.StateDTO unlike(Long ootdId, Long memberId);
}
