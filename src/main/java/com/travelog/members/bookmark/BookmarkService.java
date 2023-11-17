package com.travelog.members.bookmark;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class BookmarkService {
    @Autowired
    private final BookmarkRepository bookmarkRepository;

    // 북마크 조회
    @Transactional(readOnly = true)
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
        Bookmark bookmark = bookmarkRepository.findByMemberIdAndBoardId(memberId, boardId);
        bookmarkRepository.delete(bookmark);
    }
}
