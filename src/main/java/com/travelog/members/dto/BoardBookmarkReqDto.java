package com.travelog.members.dto;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;

@Data
public class BoardBookmarkReqDto {
    private String token;
    private Long boardId;
}
