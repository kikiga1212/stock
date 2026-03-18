package com.example.stock.DTO;

import com.example.stock.Entity.MemberEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long pid;               //상품번호
    private MemberEntity seller;    //등록한 회원번호
    private String pname;           //케이블명칭
    private String content;         //설명
    private String manufacturer;    //제조사
    private String category;        //종류(광,UPT,동축)
    private Long price;             //미터당 단가
    private Integer stock;          //현재 보유 수량(m 또는 unit)
    private String unit;            //단위(m 또는 unit)
    private String img;             //대표 이미지파일
    private List<ProductImageDTO> imageList;//상세 이미지 파일
    private LocalDateTime regDate;  //등록일자


    //private Integer cartCount;      //장바구니에 담긴 횟수
}
