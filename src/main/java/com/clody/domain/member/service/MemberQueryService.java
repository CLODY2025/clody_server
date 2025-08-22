package com.clody.domain.member.service;

import com.clody.domain.member.dto.MemberResponseDTO;
import com.clody.domain.member.entity.Member;
import com.clody.domain.member.exception.MemberErrorCode;
import com.clody.domain.member.exception.MemberException;
import com.clody.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public MemberResponseDTO.CheckNickname checkNickname(String nickname) {
        boolean available = !memberRepository.existsByNickname(nickname);
        String message = available ? "사용 가능한 닉네임입니다" : "이미 사용중인 닉네임입니다";
        
        log.info("닉네임 중복 확인 - nickname: {}, available: {}", nickname, available);
        
        return MemberResponseDTO.CheckNickname.builder()
                .available(available)
                .message(message)
                .build();
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));
    }

    public Member findByEmailAndVerified(String email) {
        return memberRepository.findByEmailAndVerified(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));
    }

    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean existsByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public MemberResponseDTO.MemberInfo getMemberInfo(Long memberId) {
        Member member = findById(memberId);
        return MemberResponseDTO.MemberInfo.from(member);
    }
}