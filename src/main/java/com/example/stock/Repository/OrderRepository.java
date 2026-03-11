package com.example.stock.Repository;

import com.example.stock.Entity.MemberEntity;
import com.example.stock.Entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    //내가 구매한 내역
    List<OrderEntity> findByBuyer(MemberEntity buyer);
    //판매자입장에서 들어온 주문 확인(Product를 거쳐 seller 확인)
    List<OrderEntity> findByProduct_Seller(MemberEntity seller);
}
