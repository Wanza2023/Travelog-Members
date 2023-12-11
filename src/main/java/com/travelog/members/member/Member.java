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
    private String nickName;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private LocalDate birth;

    private char gender;

    // pfp(profile picture): 프로필 이미지
    private String pfp;


    public void updateMember(String email, String nickName, LocalDate birth, char gender) {
        this.email = email;
        this.nickName = nickName;
        this.birth = birth;
        this.gender = gender;
    }

    public void updatePassword(String password) {
        this.password = password;
    }



    @Builder
    public Member(String email, String password, String nickName, MemberRole role, LocalDate birth, char gender, String pfp) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.role = role;
        this.birth = birth;
        this.gender = gender;
        this.pfp = pfp;
    }
}
