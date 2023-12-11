package com.travelog.members.dto.resp;

import com.travelog.members.member.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRespDto {

    @ApiModelProperty(notes = "회원 아이디", example = "abc@gachon.ac.kr")
    private String email;

    @ApiModelProperty(notes = "회원 닉네임")
    private String nickName;

    @ApiModelProperty(notes = "회원 프로필 이미지")
    private String pfp;

    @ApiModelProperty(notes = "토큰")
    private String token;

    public LoginRespDto(Member member) {
        this.email = member.getEmail();
        this.nickName = member.getNickName();
        this.pfp = member.getPfp();
    }
}
