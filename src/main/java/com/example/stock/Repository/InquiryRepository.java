package com.example.stock.Repository;

import com.example.stock.Entity.InquiryEntity;
import com.example.stock.Entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<InquiryEntity, Long> {
    //작성자(MemberEntity)를 기준으로 상담내역을 최신순(iid 내림차순)으로 조회
    //InquiryService의 getMyInquiries에서 사용
    List<InquiryEntity> findByWriterOrderByIidDesc(MemberEntity writer);
}
