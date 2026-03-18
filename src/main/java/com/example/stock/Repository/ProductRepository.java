package com.example.stock.Repository;

import com.example.stock.Entity.MemberEntity;
import com.example.stock.Entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    //특정 판매자의 전체 재고 확인
    List<ProductEntity> findBySeller(MemberEntity seller);
    //케이블 이름으로 검색
    Page<ProductEntity> findByPnameContaining(String pname, Pageable pageable);
}
