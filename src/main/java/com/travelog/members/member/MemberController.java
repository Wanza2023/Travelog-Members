package com.travelog.members.member;

import com.travelog.members.dto.resp.MemberProfileResDto;
import com.travelog.members.dto.req.LoginReqDto;
import com.travelog.members.dto.req.SignupReqDto;
import com.travelog.members.dto.req.pwReqDto;
import com.travelog.members.dto.resp.CMRespDto;
import com.travelog.members.dto.resp.LoginRespDto;
import com.travelog.members.dto.resp.MemberRespDto;
import com.travelog.members.dto.resp.PwInquiryRespDto;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @ApiOperation(value = "회원가입", notes = "이메일, 비밀번호, 닉네임, 생년월일, 성별, 프로필 사진을 통해 회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupReqDto dto, BindingResult bindingResult) {

        try {
            bindingResultErrorsCheck(bindingResult);
            memberService.checkDuplicate(dto.getEmail(), dto.getNickName());  //하나라도 존재하면 IllegalArgumentException
            Long memberId = memberService.join(dto);
            return new ResponseEntity<>(CMRespDto.builder().isSuccess(true).msg("회원가입 완료").body("member_id: " + memberId).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(false)
                    .msg("회원가입 실패")
                    .body(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "로그인", notes = "이메일, 비밀번호로 로그인을 진행합니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReqDto dto, BindingResult bindingResult) {

        try {
            bindingResultErrorsCheck(bindingResult);
            LoginRespDto respDto = memberService.login(dto);
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true)
                    .msg("로그인 성공")
                    .body(respDto).build(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(false)
                    .msg("로그인 실패")
                    .body(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "회원 프로필(닉네임) 조회", notes = "GET 요청을 보내면 해당 회원 정보를 조회합니다.")
    @GetMapping("/{memberId}")
    public MemberProfileResDto getMemeber(@PathVariable Long memberId){
        return memberService.getMember(memberId);
    }

    @ApiOperation(value = "모든 회원 조회", notes = "GET 요청을 보내면 모든 회원을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> findAll() {
        List<MemberRespDto> members = memberService.findAll();
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true)
                .msg("모든 회원입니다.")
                .body(members).build(), HttpStatus.OK);
    }

    @ApiOperation(value = "토큰으로 회원 조회")
    @GetMapping("/authorize")
    public ResponseEntity<?> authorizeMember(HttpServletRequest request) {
        try {
            MemberRespDto memberRespDto = memberService.authorizeMember(request);
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true)
                    .msg("토큰으로 회원 조회 성공")
                    .body(memberRespDto).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(false)
                    .msg("토큰으로 회원 조회 실패")
                    .body(e.getMessage()).build(), HttpStatus.OK);
        }
    }

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
                    .isSuccess(true)
                    .msg("유효한 이메일입니다. 비밀번호 재설정 가능")
                    .body(respDto).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(false)
                    .msg("실패")
                    .body(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "비밀번호 변경")
    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody pwReqDto reqDto) {
        try {
            Long memberId = reqDto.getMemberId();
            String password = reqDto.getPassword();
            memberService.updatePassword(memberId, password);
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(true)
                    .msg("비밀번호 변경 성공")
                    .body("memberId=" + memberId).build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(CMRespDto.builder()
                    .isSuccess(false)
                    .msg("비밀번호 변경 실패")
                    .body(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping("/members/nickName/{nickName}/validate")
//    public ResponseEntity<?> nickNameCheck(@PathVariable String nickName) {
//
//        boolean isSuccess = false;
//        String msg = "";
//
//        try {
//            memberService.findByNickName(nickName);
//            msg = "존재하는 닉네임입니다.";
//        } catch (IllegalArgumentException e) {
//            isSuccess = true;
//            msg = "존재하지 않는 닉네임입니다.";
//        }
//
//        return new ResponseEntity<>(CMRespDto.builder()
//                .isSuccess(isSuccess)
//                .msg(msg)
//                .body(nickName)
//                .build(), HttpStatus.OK);
//    }


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
