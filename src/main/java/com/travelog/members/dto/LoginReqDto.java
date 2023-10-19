package com.travelog.members.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
public class LoginReqDto {

    @ApiModelProperty(value = "이메일", example = "abcd@naver.com")
    @ApiParam(value = "이메일", required = true)
    @Email @NotBlank
    private String email;

    @ApiModelProperty(value = "비밀번호", example = "1234")
    @ApiParam(value = "비밀번호", required = true)
    @NotBlank
    private String password;
}
