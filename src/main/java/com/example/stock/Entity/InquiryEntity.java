package com.example.stock.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class InquiryEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iid;               //번호

    private String title;           //제목
    @Column(columnDefinition = "TEXT")
    private String content;         //내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_mid")
    private MemberEntity writer;    //작성자(나의 상담내역 조회를 위해 필요)

    private String answer;          //관리자 답변(답변이 있으면 답변완료 상태)
}
