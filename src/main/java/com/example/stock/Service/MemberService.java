package com.example.stock.Service;

import com.example.stock.DTO.MemberDTO;
import com.example.stock.Entity.MemberEntity;
import com.example.stock.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    //회원가입
    public Long signup(MemberDTO memberDTO){
        //1. 사업자등록번호 중복체크
        validateDuplicateMember(memberDTO.getBusinessNo());

        //2. DTO -> Entity 변환
        MemberEntity memberEntity = modelMapper.map(memberDTO, MemberEntity.class);

        //3. 저장후 번호 반환
        return memberRepository.save(memberEntity).getMid();
    }

    // 로그인
    public MemberDTO login(String businessNo, String password){
        MemberEntity memberEntity = memberRepository.findByBusinessNo(businessNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사업자번호입니다."));

        if(!memberEntity.getPassword().equals(password)){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return modelMapper.map(memberEntity, MemberDTO.class);
    }
    private void validateDuplicateMember(String businessNo){
        memberRepository.findByBusinessNo(businessNo)
                .ifPresent(m ->{
                    throw new IllegalStateException("이미 가입된 사업자등록번호입니다.");
                });
    }

    // 정보 수정
    public void updateMember(MemberDTO memberDTO) {
        MemberEntity member = memberRepository.findById(memberDTO.getMid())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 비밀번호 제외, 수정가능한 필드 업데이트
        member.setName(memberDTO.getName());
        member.setPhone(memberDTO.getPhone());
        member.setAddress(memberDTO.getAddress());
        member.setCompanyName(memberDTO.getCompanyName());
        member.setEmail(memberDTO.getEmail());

        // @Transactional이 붙어있으므로 별도의 save 없이도 DB에 반영됩니다.
    }

    // 비밀번호 재설정
    public void updatePassword(Long mid, String newPassword) {
        MemberEntity member = memberRepository.findById(mid).get();
        member.setPassword(newPassword);
    }

    // 회원 탈퇴
    public void deleteMember(Long mid) {
        memberRepository.deleteById(mid);
    }
}//end
