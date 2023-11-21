package com.travelog.members.member;

import com.travelog.members.auth.JwtTokenProvider;
import com.travelog.members.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
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
    public Long join(SignupReqDto dto) {
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

        return member.getId();
    }

    @Transactional
    public LoginRespDto login(LoginReqDto loginReqDto) {

        Member member = memberRepository.findByEmail(loginReqDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

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
    public MemberRespDto authorizeMember(HttpServletRequest request) {

        String token = jwtTokenProvider.resolveToken(request);

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        String email = jwtTokenProvider.getUserPk(token);
        Member findMember = findByEmail(email);
        return new MemberRespDto(findMember);


    }

    public MemberProfileResDto authMember(String token){
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        String email = jwtTokenProvider.getUserPk(token);
        Member findMember = findByEmail(email);
        return new MemberProfileResDto(findMember);
    }

    // 회원 프로필(닉네임) 조회
    public MemberProfileResDto getMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 id입니다."));
        return new MemberProfileResDto(member);
    }

    public List<MemberRespDto> findAll() {
        List<Member> members = memberRepository.findAll();
        List<MemberRespDto> result = members.stream()
                .map(MemberRespDto::new)
                .collect(Collectors.toList());
        return result;
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 id 입니다."));
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 이메일입니다."));
    }

    public Member findByNickName(String nickName) {
        return memberRepository.findByNickName(nickName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 닉네임입니다."));
    }

    public void checkDuplicate(String email, String nickName) {
        Optional<Member> byEmail = memberRepository.findByEmail(email);
        Optional<Member> byNickName = memberRepository.findByNickName(nickName);
        if (byEmail.isPresent()) throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        if (byNickName.isPresent()) throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
    }


    public boolean isPasswordSame(Member member, String password) {
        return passwordEncoder.matches(password, member.getPassword());
    }
}
