package com.travelog.members.dto.resp;

import com.travelog.members.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberProfileResDto {
    private Long id;
    private String nickname;

    public MemberProfileResDto(Member member){
        this.id = member.getId();
        this.nickname = member.getNickName();
    }
}
