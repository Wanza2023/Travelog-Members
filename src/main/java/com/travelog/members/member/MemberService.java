package com.travelog.members.member;

import com.travelog.members.auth.JwtTokenProvider;
import com.travelog.members.dto.LoginReqDto;
import com.travelog.members.dto.LoginRespDto;
import com.travelog.members.dto.MemberRespDto;
import com.travelog.members.dto.SignupReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * 등록
     */
    @Transactional
    public void join(SignupReqDto dto) {

        Optional<Member> memberCheck = memberRepository.findByEmail(dto.getEmail());
        if (memberCheck.isPresent()) throw new RuntimeException("존재하는 아이디입니다.");

        Member member = Member.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickName(dto.getNickName())
                .role(MemberRole.MEMBER)
                .birth(dto.getBirth())
                .gender(dto.getGender())
                .pfp(dto.getPfp())
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public LoginRespDto login(LoginReqDto loginReqDto) {

        Member member = memberRepository.findByEmail(loginReqDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 사용자입니다."));

        if (!isPasswordSame(member, loginReqDto.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        // 로그인에 성공하면 email, roles 로 토큰 생성 후 반환
        String token = jwtTokenProvider.createToken(member.getEmail());

        LoginRespDto loginRespDto = new LoginRespDto();
        loginRespDto.setEmail(loginReqDto.getEmail());
        loginRespDto.setToken(token);
        return loginRespDto;
    }

    /**
     * 수정
     */
//    @Transactional
//    public void updatePassword(Member member, String password) {
//        if (isPasswordSame(member, password)) {
//            throw new IllegalArgumentException("동일한 비밀번호입니다.");
//        }
//
//        member.updatePassword(passwordEncoder.encode(password));
//    }

    /**
     * 삭제
     */
    @Transactional
    public void deleteById(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    @Transactional
    public void deleteByEmail(String email) {
        Member member = findByEmail(email);
        memberRepository.delete(member);
    }

    /**
     * 조회
     */
    public List<MemberRespDto> findAll() {
        List<Member> members = memberRepository.findAll();
        List<MemberRespDto> result = members.stream()
                .map(MemberRespDto::new)
                .collect(Collectors.toList());
        return result;
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 id 입니다."));
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 이메일입니다."));
    }


    public boolean isPasswordSame(Member member, String password) {
        return passwordEncoder.matches(password, member.getPassword());
    }
}
