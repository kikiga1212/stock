package com.example.stock.DTO;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoDTO {
    private int startPage;      //시작페이지 번호
    private int endPage;        //끝페이지 번호
    private int pre;            //이전페이지 번호
    private int current;        //현재페이지 번호
    private int next;           //다음페이진 번호
    private int last;           //마지막페이지 번호
    private long totalRecords;      //전체페이지 수
}
