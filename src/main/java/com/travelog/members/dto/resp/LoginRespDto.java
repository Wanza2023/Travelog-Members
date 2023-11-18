package com.travelog.members.dto.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRespDto {

    @ApiModelProperty(notes = "회원 아이디", example = "abc@gachon.ac.kr")
    private String email;

    @ApiModelProperty(notes = "토큰")
    private String token;
}
