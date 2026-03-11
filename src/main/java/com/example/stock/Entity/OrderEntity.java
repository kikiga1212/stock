package com.example.stock.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class OrderEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oid;               //주문번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_mid")
    private MemberEntity buyer;     //구매자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_pid")
    private ProductEntity product;  //구매상품
    private int orderAmount;        //주문수량
    private Long totalPrice;        //총 결제금액

    @Column(length = 50)
    private String status;          //주문상태(결제대기, 발송완료, 거래완료)
}
