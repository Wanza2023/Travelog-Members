package com.travelog.members.member;

import com.travelog.members.board.BoardServiceFeignClient;
import com.travelog.members.dto.board.BoardTotalViewsDto;
import com.travelog.members.dto.resp.*;
import com.travelog.members.dto.req.LoginReqDto;
import com.travelog.members.dto.req.SignupReqDto;
import com.travelog.members.dto.req.UpdateReqDto;
import com.travelog.members.dto.req.pwReqDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final BoardServiceFeignClient boardServiceFeignClient;

    /**
     * 회원가입
     */
    @ApiOperation(value = "회원가입", notes = "이메일, 비밀번호, 닉네임, 생년월일, 성별, 프로필 사진을 통해 회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupReqDto dto, BindingResult bindingResult) {

        try {
            bindingResultErrorsCheck(bindingResult);
            memberService.checkDuplicate(dto.getEmail(), dto.getNickName());  //하나라도 존재하면 IllegalArgumentException
            Long memberId = memberService.join(dto);
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true).msg("회원가입 완료").body("member_id: " + memberId).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(false).msg("회원가입 실패").body(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 로그인
     */
    @ApiOperation(value = "로그인", notes = "이메일, 비밀번호로 로그인을 진행합니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReqDto dto, BindingResult bindingResult) {

        try {
            bindingResultErrorsCheck(bindingResult);
            LoginRespDto respDto = memberService.login(dto);
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true).msg("로그인 성공").body(respDto).build(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(false).msg("로그인 실패").body(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 회원 탈퇴
     */
    @ApiOperation(value = "회원 탈퇴")
    @DeleteMapping
    public ResponseEntity<?> deleteMember(HttpServletRequest request) {
        try {
            MemberRespDto member = memberService.authorizeMember(request);
            Long memberId = member.getId();
            memberService.deleteById(memberId);
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true).msg("회원 탈퇴 성공").body(member.getEmail()).build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(false).msg("회원 탈퇴 실패").body(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 비밀번호 확인
     */
    @ApiOperation(value = "비밀번호 확인")
    @PostMapping("/validate/passwd")
    public ResponseEntity<?> validatePasswd(HttpServletRequest request, @RequestBody String passwd) {

        try {
            memberService.validatePasswd(request, passwd);
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true).msg("비밀번호 확인 성공").body("비밀번호가 동일합니다.").build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(false).msg("비밀번호 확인 실패").body(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 비밀번호 찾기
     */
    @ApiOperation(value = "비밀번호 찾기")
    @PostMapping("/pwInquiry")
    public ResponseEntity<?> pwInquiry(@RequestBody String email) {
        //이메일 입력
        //해당 이메일로 인증번호 발송
        //틀릴시 실패, 성공시 비밀번호 재설정
        //On-Premise 서버인 관계로 이메일 인증 건너뛰기
        try {
            Long memberId = memberService.findByEmail(email).getId();
            String href = "http://172.16.210.131:8081/members/password";
            String method = "PATCH";
            PwInquiryRespDto respDto = new PwInquiryRespDto(memberId, href, method);
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true).msg("유효한 이메일입니다. 비밀번호 재설정 가능").body(respDto).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(false).msg("실패").body(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 비밀번호 변경
     */
    @ApiOperation(value = "비밀번호 변경")
    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody pwReqDto dto) {
        try {
            Long memberId = dto.getMemberId();
            String password = dto.getPassword();
            memberService.updatePassword(memberId, password);
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true).msg("비밀번호 변경 성공").body("memberId=" + memberId).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(false).msg(e.getMessage()).body(e.getCause()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "회원정보 수정")
    @PatchMapping("/info")
    public ResponseEntity<?> updateMember(HttpServletRequest request, @RequestBody UpdateReqDto dto) {
        try {
            memberService.updateMember(request, dto);
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true).msg("회원정보 수정 성공").body(dto).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(false).msg(e.getMessage()).body(e.getCause()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 조회
     */
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularBlog() {
        try {
            List<PopularRespDto> result = memberService.getPopular();
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true).msg("인기 블로그 조회 성공").body(result).build(), HttpStatus.OK);
        } catch (Exception e) {
            log.warn(e.getMessage(), e.getCause());
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(false).msg(e.getMessage()).body(e.getCause()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "회원 프로필(닉네임) 조회", notes = "GET 요청을 보내면 해당 회원 정보를 조회합니다.")
    @GetMapping("/{memberId}")
    public MemberRespDto getMember(@PathVariable Long memberId){
        return memberService.getMember(memberId);
    }

    @ApiOperation(value = "모든 회원 조회", notes = "GET 요청을 보내면 모든 회원을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> findAll() {
        List<MemberRespDto> members = memberService.findAll();
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg("모든 회원입니다.").body(members).build(), HttpStatus.OK);
    }

    @ApiOperation(value = "토큰으로 회원 조회")
    @GetMapping("/authorize")
    public ResponseEntity<?> authorizeMember(HttpServletRequest request) {
        try {
            MemberRespDto memberRespDto = memberService.authorizeMember(request);
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true).msg("토큰으로 회원 조회 성공").body(memberRespDto).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(false).msg("토큰으로 회원 조회 실패").body(e.getMessage()).build(), HttpStatus.OK);
        }
    }

    @ApiOperation(value = "닉네임, 프로필 이미지 조회")
    @PostMapping("/briefInfo")
    public List<MemberBriefInfoDto> briefInfo(@RequestBody List<Long> memberIds) {
        return memberService.getBriefInfoById(memberIds);
    }

    /**
     * 중복 확인
     */
    @ApiOperation(value = "이메일 중복 확인")
    @GetMapping("/validate/email/{email}")
    public ResponseEntity<?> validateEmail(@PathVariable String email) {

        boolean result = memberService.existByEmail(email);
        String msg = result ? "이미 존재하는 이메일입니다." : "사용 가능한 이메일입니다.";

        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg(msg).body(!result).build(), HttpStatus.OK);
    }

    @ApiOperation(value = "닉네임 중복 확인")
    @GetMapping("/validate/nickname/{nickName}")
    public ResponseEntity<?> validateNickName(@PathVariable String nickName) {

        boolean result = memberService.existByNickName(nickName);
        String msg = result ? "이미 존재하는 닉네임입니다." : "사용 가능한 닉네임입니다.";

        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true).msg(msg).body(!result).build(), HttpStatus.OK);
    }


    //validation 체크
    private void bindingResultErrorsCheck(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError fe : bindingResult.getFieldErrors()) {
                errorMap.put(fe.getField(), fe.getDefaultMessage());
            }
            throw new RuntimeException(errorMap.toString());
        }
    }
}
