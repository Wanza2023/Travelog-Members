package com.travelog.members.bookmark;

import com.travelog.members.board.BoardDto;
import com.travelog.members.board.BoardServiceFeignClient;
import com.travelog.members.dto.BoardBookmarkReqDto;
import com.travelog.members.dto.resp.MemberRespDto;
import com.travelog.members.member.MemberService;
import com.travelog.members.dto.resp.CMRespDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookmark")
public class BookmarkController {

    private final BoardServiceFeignClient boardServiceFeignClient;
    private final BookmarkService bookmarkService;
    private final MemberService memberService;

    // 북마크 리스트 가져오기
    @GetMapping(value = "/{memberId}")
    public ResponseEntity<?> getBookmark(@PathVariable Long memberId){
        List<Long> boardIds = bookmarkService.getBoardIds(memberId);
        try {
            List<BoardDto> bookmarks = boardServiceFeignClient.getBoards(boardIds);
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true).msg("해당 회원의 북마크 리스트")
                    .body(bookmarks).build(), HttpStatus.OK);
        } catch (FeignException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true).msg("북마크 가져오기 실패")
                    .body(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // 북마크 저장
    @PostMapping
    public ResponseEntity<?> saveBookmark(@Valid @RequestBody Bookmark bookmarkReq, HttpServletRequest request){
        MemberRespDto member = memberService.authorizeMember(request);
        Bookmark bookmark = new Bookmark(member.getId(), bookmarkReq.getBoardId());
        Long res = bookmarkService.saveBookmark(bookmark);
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg("북마크에 저장되었습니다.")
                .body(res).build(), HttpStatus.CREATED);
    }

    // 북마크 삭제
    @DeleteMapping(value = "/{boardId}")
    public ResponseEntity<?> deleteBookmark(@PathVariable Long boardId, HttpServletRequest request){
        MemberRespDto member = memberService.authorizeMember(request);
        try {
            bookmarkService.deleteBookmark(member.getId(), boardId);
            return new ResponseEntity<>(CMRespDto.builder().isSuccess(true)
                    .msg("북마크에서 삭제되었습니다.").build(), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(CMRespDto.builder().isSuccess(false)
                    .body(e.getMessage()).msg("북마크 삭제 실패").build(), HttpStatus.OK);
        }
    }

    // 북마크 확인
    @PostMapping(value = "/isBookmark")
    public boolean isBookmark(@Valid @RequestBody BoardBookmarkReqDto dto){
        try {
            MemberRespDto member = memberService.authMember(dto.getToken());
            return bookmarkService.isBookmark(member.getId(), dto.getBoardId());
            // return memberProfileResDto.getNickname();
        } catch (IllegalArgumentException e){
            return false;
        }
    }
}
