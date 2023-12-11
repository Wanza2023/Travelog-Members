package com.travelog.members.dto.req;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter @Setter
public class SignupReqDto {

    @ApiModelProperty(value = "회원 아이디", example = "abcd@naver.com")
    @ApiParam(value = "이메일", required = true)
    @Email @NotBlank
    private String email;

    @ApiModelProperty(value = "비밀번호", example = "1234")
    @ApiParam(value = "비밀번호")
    @NotBlank
    private String password;

    @ApiModelProperty(value = "닉네임", example = "한글 닉네임")
    @NotBlank
    private String nickName;

    @ApiModelProperty(value = "생년월일", example = "2000-01-01")
//    @ApiParam(value = "생년월일", format = "yyyy-MM-dd", example = "yyyy-MM-dd")
    private LocalDate birth;

    @ApiModelProperty(value = "성별", example = "M")
//    @ApiParam(value = "성별", example = "M")
    private char gender;

    @ApiModelProperty(value = "프로필 사진")
    @ApiParam(value = "프로필 사진")
    private String pfp;
}
