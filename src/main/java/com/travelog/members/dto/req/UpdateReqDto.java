package com.travelog.members.dto.req;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateReqDto {
    private String nickName;
    private LocalDate birth;
    private char gender;
    private String pfp;
}
