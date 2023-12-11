package com.travelog.members.dto.req;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class pwReqDto {

    @ApiModelProperty(value = "memberId")
    @ApiParam(value = "memberId")
    private Long memberId;

    @ApiModelProperty(value = "비밀번호", example = "1234")
    @ApiParam(value = "비밀번호", required = true)
    @NotBlank
    private String password;
}
