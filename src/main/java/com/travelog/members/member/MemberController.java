package com.travelog.members.member;

import com.travelog.members.dto.*;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @ApiOperation(value = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupReqDto dto, BindingResult bindingResult) {
        bindingResultErrorsCheck(bindingResult);
        memberService.join(dto);
        return new ResponseEntity<>(CMRespDto.builder().isSuccess(true).msg("회원가입 완료").build(), HttpStatus.OK);
    }

    @ApiOperation(value = "로그인")
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

    @ApiOperation(value = "모든 회원 조회")
    @GetMapping("/members")
    public ResponseEntity<?> findAll() {
        List<MemberRespDto> members = memberService.findAll();
        return new ResponseEntity<>(CMRespDto.builder()
                .isSuccess(true)
                .msg("모든 회원입니다.")
                .body(members).build(), HttpStatus.OK);
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
