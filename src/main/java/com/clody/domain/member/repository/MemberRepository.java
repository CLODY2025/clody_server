package com.clody.domain.member.repository;

import com.clody.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(Long memberId);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByEmailAndIsEmailVerified(String email, Boolean isEmailVerified);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    
    default Optional<Member> findByEmailAndVerified(String email) {
        return findByEmailAndIsEmailVerified(email, true);
    }
}
