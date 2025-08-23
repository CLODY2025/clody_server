package com.clody.domain.ootd.service.ootdLikeCommandService;


import com.clody.domain.member.entity.Member;
import com.clody.domain.member.repository.MemberRepository;
import com.clody.domain.ootd.dto.OotdLikeResponseDTO;
import com.clody.domain.ootd.entity.Ootd;
import com.clody.domain.ootd.entity.OotdLike;
import com.clody.domain.ootd.exception.OotdErrorCode;
import com.clody.domain.ootd.exception.OotdException;
import com.clody.domain.ootd.repository.OotdLikeRepository;
import com.clody.domain.ootd.repository.OotdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OotdLikeCommandServiceImpl implements OotdLikeCommandService {
    private final OotdRepository ootdRepository;
    private final MemberRepository memberRepository;
    private final OotdLikeRepository likeRepository;


    @Transactional
    @Override
    public OotdLikeResponseDTO.StateDTO like(Long ootdId, Long memberId) {
        Ootd ootd = ootdRepository.findById(ootdId)
                .orElseThrow(() -> new OotdException(OotdErrorCode.NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new OotdException(OotdErrorCode.MEMBER_NOT_FOUND));

        if (!likeRepository.existsByOotd_IdAndMember_Id(ootdId, memberId)) {
            try {
                likeRepository.save(OotdLike.builder().ootd(ootd).member(member).build());
            } catch (DataIntegrityViolationException ignore) { //동시 요청처리 무시
            }
        }
        long count = likeRepository.countByOotd_Id(ootdId);
        return new OotdLikeResponseDTO.StateDTO(true, count);
    }

    @Transactional
    @Override
    public OotdLikeResponseDTO.StateDTO unlike(Long ootdId, Long memberId) {
        ootdRepository.findById(ootdId)
                .orElseThrow(() -> new OotdException(OotdErrorCode.NOT_FOUND));
        memberRepository.findById(memberId)
                .orElseThrow(() -> new OotdException(OotdErrorCode.MEMBER_NOT_FOUND));

        likeRepository.deleteByOotdIdAndMemberId(ootdId, memberId);
        long count = likeRepository.countByOotd_Id(ootdId);
        return new OotdLikeResponseDTO.StateDTO(false, count);
    }
}
