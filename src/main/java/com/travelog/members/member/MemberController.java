package com.travelog.members.member;

import com.travelog.members.dto.*;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
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

    @ApiOperation(value = "모든 회원 조회", notes = "GET 요청을 보내면 모든 회원을 조회합니다.")
    @GetMapping("/members")
    public ResponseEntity<?> findAll() {
        List<MemberRespDto> members = memberService.findAll();
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true)
                .msg("모든 회원입니다.")
                .body(members).build(), HttpStatus.OK);
    }

    @ApiOperation(value = "토큰으로 회원 조회")
    @GetMapping("/members/authorize/{token}")
    public ResponseEntity<?> authorizeMember(@PathVariable String token) {
        MemberRespDto memberRespDto = memberService.authorizeMember(token);
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true)
                .msg("토큰으로 회원 조회")
                .body(memberRespDto).build(), HttpStatus.OK);
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
