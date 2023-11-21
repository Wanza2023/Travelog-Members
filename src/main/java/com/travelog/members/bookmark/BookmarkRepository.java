package com.travelog.members.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // 북마크 목록 불러오기
    @Query("select b.boardId from Bookmark b where b.memberId = :memberId")
    List<Long> findByMemberId(Long memberId);

    // 북마크한 게시글 검색
    Optional<Bookmark> findByMemberIdAndBoardId(Long memberId, Long boardId);
}
