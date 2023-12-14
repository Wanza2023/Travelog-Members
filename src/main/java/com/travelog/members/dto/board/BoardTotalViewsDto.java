package com.travelog.members.dto.board;

import lombok.Data;

@Data
public class BoardTotalViewsDto {
    private Long memberId;
    private Long totalViews;
}
