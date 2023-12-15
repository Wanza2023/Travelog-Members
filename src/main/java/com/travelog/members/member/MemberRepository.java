package com.travelog.members.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickName(String nickName);
    boolean existsByEmail(String email);
    boolean existsByNickName(String nickName);

    @Query("select m.pfp from Member m where m.nickName = :nickName")
    String getPfpByNickName(String nickName);
}
