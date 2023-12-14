package com.travelog.members.dto.resp;

import com.travelog.members.member.Member;
import lombok.Data;

@Data
public class MemberBriefInfoDto {

    private Long memberId;
    private String nickName;
    private String pfp;

    public MemberBriefInfoDto(Member member) {
        this.memberId = member.getId();
        this.nickName = member.getNickName();
        this.pfp = member.getPfp();
    }
}
