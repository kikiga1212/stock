package com.example.stock.Service;

import com.example.stock.DTO.InquiryDTO;
import com.example.stock.Entity.InquiryEntity;
import com.example.stock.Entity.MemberEntity;
import com.example.stock.Repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final ModelMapper modelMapper;

    //상담등록
    public Long registerInquiry(InquiryDTO inquiryDTO){
        InquiryEntity inquiry = modelMapper.map(inquiryDTO, InquiryEntity.class);
        return inquiryRepository.save(inquiry).getIid();
    }

    //나의 상담내역조회(최신순)
    @Transactional
    public List<InquiryDTO> getMyInquiries(MemberEntity writer){
        List<InquiryEntity> inquiries = inquiryRepository.findByWriterOrderByIidDesc(writer);
        return inquiries.stream()
                .map(entity -> modelMapper.map(entity, InquiryDTO.class))
                .collect(Collectors.toList());
    }

    //상담 상세조회
    @Transactional(readOnly = true)
    public InquiryDTO getInquiry(Long iid){
        InquiryEntity inquiry = inquiryRepository.findById(iid)
                .orElseThrow(() -> new IllegalArgumentException("해당 상담글이 존재하지 않습니다."));
        return modelMapper.map(inquiry, InquiryDTO.class);
    }

    //상담 수정(답변이 달리기 전까지만 수정 가능하도록)
    public void updateInquiry(InquiryDTO inquiryDTO){
        InquiryEntity inquiry = inquiryRepository.findById(inquiryDTO.getIid())
                .orElseThrow(() -> new IllegalArgumentException("해당 상담글이 존재하지 않습니다."));

        if(inquiry.getAnswer() != null){
            throw new IllegalStateException("답변이 완료된 문의사항은 수정할 수 없습니다.");
        }
        inquiry.setTitle(inquiryDTO.getTitle());
        inquiry.setContent(inquiryDTO.getContent());
    }
    //상담 삭제
    public void deleteInquiry(Long iid){
        inquiryRepository.deleteById(iid);
    }
}
