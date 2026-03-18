package com.example.stock.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "product")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inum; // 이미지 번호

    private String uuid; // 중복 방지 ID
    private String imgName; // 실제 파일 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_pid") // 외래키
    private ProductEntity product;
}