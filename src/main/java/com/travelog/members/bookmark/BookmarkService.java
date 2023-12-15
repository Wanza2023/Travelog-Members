package com.travelog.members.bookmark;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    // 북마크 조회
    public List<Long> getBoardIds(Long memberId){
        return bookmarkRepository.findByMemberId(memberId);
    }

    // 북마크 저장
    @Transactional
    public Long saveBookmark(Bookmark bookmark){
        bookmarkRepository.save(bookmark);
        return bookmark.getBookmarkId();
    }

    // 북마크 삭제
    @Transactional
    public void deleteBookmark(Long memberId, Long boardId){
        Bookmark bookmark = bookmarkRepository.findByMemberIdAndBoardId(memberId, boardId)
                .orElseThrow(()->new IllegalArgumentException("북마크 하지 않은 게시글"));
        bookmarkRepository.delete(bookmark);
    }

    // 북마크 확인
    @Transactional
    public boolean isBookmark(Long memberId, Long boardId) {
        Optional<Bookmark> bookmark = bookmarkRepository.findByMemberIdAndBoardId(memberId, boardId);
        return bookmark.isPresent();
    }
}
