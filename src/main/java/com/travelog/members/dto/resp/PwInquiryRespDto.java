package com.travelog.members.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class PwInquiryRespDto {

    private Long memberId;
    private String href;
    private String method;
}
