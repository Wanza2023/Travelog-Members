package com.travelog.members.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateReqDto {

    @ApiModelProperty(value = "닉네임")
    private String nickName;

    @ApiModelProperty(value = "생년월일", example = "2000-01-01")
    private LocalDate birth;

    @ApiModelProperty(value = "성별", example = "M")
    private char gender;

    @ApiModelProperty(value = "프로필 사진")
    private String pfp;
}
