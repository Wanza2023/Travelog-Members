package com.travelog.members.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class pwReqDto {
    private Long memberId;
    private String password;
}
