package com.travelog.members.dto.resp;

import com.travelog.members.member.Member;
import com.travelog.members.member.MemberRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class MemberRespDto {

    private Long id;
    private String email;
    private String nickname;
    private MemberRole role;
    private LocalDate birth;
    private char gender;

    public MemberRespDto(Member member) {
        id = member.getId();
        email = member.getEmail();
        nickname = member.getNickName();
        role = member.getRole();
        birth = member.getBirth();
        gender = member.getGender();
    }
}
