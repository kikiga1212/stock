package com.example.stock.DTO;

import com.example.stock.Entity.MemberEntity;
import com.example.stock.Entity.ProductEntity;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long oid;               //주문번호
    private MemberEntity buyer;     //구매자
    private ProductEntity product;  //구매상품
    private int orderAmount;        //주문수량
    private Long totalPrice;        //총 결제금액
    private String status;
}
