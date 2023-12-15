package com.travelog.members.dto.board;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookmarkDto {
    private Long boardId;
    private String nickname;
    private String local;
    private String title;
    private String contents;
    private String summary;
    private List<String> hashtags;
    private LocalDateTime createdAt;
    private String pfp;

    public BookmarkDto(BoardDto boardDto, String pfp) {
        this.boardId = boardDto.getBoardId();
        this.nickname = boardDto.getNickname();
        this.local = boardDto.getLocal();
        this.title = boardDto.getTitle();
        this.contents = boardDto.getContents();
        this.summary = boardDto.getSummary();
        this.hashtags = boardDto.getHashtags();
        this.createdAt = boardDto.getCreatedAt();
        this.pfp = pfp;
    }
}
