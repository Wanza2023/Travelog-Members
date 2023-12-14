package com.travelog.members.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PopularRespDto {
    private Long memberId;
    private String nickName;
    private String pfp;
    private Long totalViews;
}
