package com.example.stock.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product")
@Getter @Setter
@ToString @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;               //상품번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_mid")
    private MemberEntity seller;    //등록한 회원번호

    @Column(length = 100, nullable = false)
    private String pName;          //케이블명칭
    @Column(length = 50)
    private String manufacturer;    //제조사
    @Column(length = 50)
    private String category;        //종류(광,UPT,동축)
    private Long price;             //미터당 단가
    private Integer stock;          //현재 보유 수량
    @Column(length = 10)
    private String unit;            //단위 "m" 또는 "unit"저장
    @Column(name="img")
    private String img;             //이미지파일
}
