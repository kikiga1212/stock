package com.example.stock.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private MemberEntity seller;    //판매자

    @Column(length = 100, nullable = false)
    private String pname;           //케이블명칭
    @Column(length = 500)
    private String content;         //설명
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

    // 💡 이미지 리스트 추가 (Cascade를 통해 상품 저장 시 이미지도 같이 저장됨)
    @Builder.Default
    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<ProductImage> imageList = new ArrayList<>();

    // 💡 이미지 추가를 도와주는 편의 메서드
    public void addImage(ProductImage productImage) {
        imageList.add(productImage);
        productImage.setProduct(this);
    }
}
