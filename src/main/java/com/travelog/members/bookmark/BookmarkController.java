package com.travelog.members.bookmark;

import com.travelog.members.dto.board.BoardDto;
import com.travelog.members.board.BoardServiceFeignClient;
import com.travelog.members.dto.board.BoardBookmarkReqDto;
import com.travelog.members.dto.board.BookmarkDto;
import com.travelog.members.dto.resp.MemberRespDto;
import com.travelog.members.member.MemberService;
import com.travelog.members.dto.resp.CMRespDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmark")
public class BookmarkController {

    private final BoardServiceFeignClient boardServiceFeignClient;
    private final BookmarkService bookmarkService;
    private final MemberService memberService;

    // 북마크 리스트 가져오기
    @GetMapping(value = "/{memberId}")
    public ResponseEntity<?> getBookmark(@PathVariable Long memberId){

        try {
            List<BookmarkDto> result = new ArrayList<>();
            List<Long> boardIds = bookmarkService.getBoardIds(memberId);
            List<BoardDto> boardDtos = boardServiceFeignClient.getBoards(boardIds);
            for (BoardDto boardDto : boardDtos) {
                String nickName = boardDto.getNickname();
                String pfp = memberService.getPfpByNickName(nickName);
                BookmarkDto bookmarkDto = new BookmarkDto(boardDto, pfp);
                result.add(bookmarkDto);
            }
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true).msg("해당 회원의 북마크 리스트").body(result).build(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true).msg("북마크 가져오기 실패").body(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/bookmarklist")
    public List<Long> getBookmarkByToken(@RequestBody String token) {
        MemberRespDto memberRespDto = memberService.authMember(token);
        Long memberId = memberRespDto.getId();
        List<Long> boardIds = bookmarkService.getBoardIds(memberId);
        return boardIds;
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
