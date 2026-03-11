package com.example.stock.DTO;

import com.example.stock.Entity.MemberEntity;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long pid;               //상품번호
    private MemberEntity seller;    //등록한 회원번호
    private String pName;          //케이블명칭
    private String manufacturer;    //제조사
    private String category;        //종류(광,UPT,동축)
    private Long price;             //미터당 단가
    private Integer stock;          //현재 보유 수량(m)
    private String unit;
}
