package com.travelog.members.bookmark;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkId;

    @Column(nullable = false)
    private Long memberId;

    @Column(unique = true, nullable = false)
    private Long boardId;

    @Builder
    public Bookmark(Long memberId, Long boardId){
        this.memberId = memberId;
        this.boardId = boardId;
    }
}
