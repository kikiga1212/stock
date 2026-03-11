package com.example.stock.DTO;

import com.example.stock.Entity.MemberEntity;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InquiryDTO {
    private Long iid;               //번호
    private String title;           //제목
    private String content;         //내용
    private MemberEntity writer;    //작성자(나의 상담내역 조회를 위해 필요)
    private String answer;          //관리자 답변(답변이 있으면 답변완료 상태)

}
