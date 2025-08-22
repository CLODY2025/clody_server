package com.clody.domain.member.repository;

import com.clody.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    
    boolean existsByEmail(String email);
    
    boolean existsByNickname(String nickname);
    
    Optional<Member> findByEmail(String email);
    
    @Query("SELECT m FROM Member m WHERE m.email = :email AND m.isEmailVerified = true")
    Optional<Member> findByEmailAndVerified(@Param("email") String email);
    
    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.email = :email AND m.isEmailVerified = true")
    boolean existsByEmailAndVerified(@Param("email") String email);
}