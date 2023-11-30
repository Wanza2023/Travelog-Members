package com.travelog.members.member;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private LocalDate birth;

    private char gender;

    // pfp(profile picture): 프로필 이미지
    @Lob @Column(columnDefinition = "blob")
    private byte[] pfp;


    public void updateMember(String email, String nickname, LocalDate birth, char gender) {
        this.email = email;
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
    }

    public void updatePassword(String password) {
        this.password = password;
    }



    @Builder
    public Member(String email, String password, String nickname, MemberRole role, LocalDate birth, char gender, byte[] pfp) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.birth = birth;
        this.gender = gender;
        this.pfp = pfp;
    }
}
