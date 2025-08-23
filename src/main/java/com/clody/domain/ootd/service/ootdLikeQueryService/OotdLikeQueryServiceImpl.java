package com.clody.domain.ootd.service.ootdLikeQueryService;

import com.clody.domain.ootd.dto.OotdLikeResponseDTO;
import com.clody.domain.ootd.repository.OotdLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OotdLikeQueryServiceImpl implements OotdLikeQueryService {
    private final OotdLikeRepository ootdLikeRepository;

    @Transactional(readOnly = true)
    @Override
    public OotdLikeResponseDTO.StateDTO state(Long ootdId, Long memberId) {
        long count = ootdLikeRepository.countByOotd_Id(ootdId);
        boolean liked = (memberId != null) && ootdLikeRepository.existsByOotd_IdAndMember_Id(ootdId, memberId);
        return new OotdLikeResponseDTO.StateDTO(liked, count);
    }

    @Transactional(readOnly = true)
    @Override
    public OotdLikeResponseDTO.CountDTO count(Long ootdId) {
        return new OotdLikeResponseDTO.CountDTO(ootdLikeRepository.countByOotd_Id(ootdId));
    }
}
