package com.example.stock.Repository;

import com.example.stock.Entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    //사업자등록번호로 회원찾기( 로그인시 사용)
    Optional<MemberEntity> findByBusinessNo(String businessNo);
}
