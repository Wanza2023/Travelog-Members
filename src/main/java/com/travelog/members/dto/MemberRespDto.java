package com.travelog.members.dto;

import com.travelog.members.member.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class MemberRespDto {

    private Long id;
    private String email;
    private String nickName;
    private LocalDate birth;
    private char gender;
    private byte[] pfp;

    public MemberRespDto(Member member) {
        id = member.getId();
        email = getEmail();
        nickName = member.getNickName();
        birth = member.getBirth();
        gender = member.getGender();
        pfp = member.getPfp();
    }
}
