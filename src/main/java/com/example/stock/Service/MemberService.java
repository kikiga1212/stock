package com.example.stock.Service;

import com.example.stock.Constant.Role;
import com.example.stock.DTO.MemberDTO;
import com.example.stock.Entity.MemberEntity;
import com.example.stock.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;


    //회원가입
    public Long signup(MemberDTO memberDTO){
        //1. DTO -> Entity 변환
        MemberEntity memberEntity = modelMapper.map(memberDTO, MemberEntity.class);

        //2. 중복가입 체크(DB 저장 전에 미리 확인하는 것이 효율적)
        memberRepository.findByBusinessNo(memberEntity.getBusinessNo())
                .ifPresent(m ->{
                    throw new IllegalArgumentException("이미 존재하는 사업자 번호입니다. ");
                });

        //3. 권한 설정
        // 사업자 번호가 있거나 특정 조건에 따라 가입 시점에 Role을 부여합니다.
        if(memberDTO.getAdminKey() != null && "ADMIN_SECRET_KEY".equals(memberDTO.getAdminKey())){//관리자 키가 있는 경우
            memberEntity.setRole(Role.ADMIN);
        }else{
            // 일반 사용자는 판매자이자 구매자이므로 통합 권한 부여
            memberEntity.setRole(Role.USER);
        }
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


    // 정보 수정
    public void updateMember(MemberDTO memberDTO) {
        MemberEntity member = memberRepository.findById(memberDTO.getMid())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 비밀번호 제외, 수정가능한 필드 업데이트
        member.setName(memberDTO.getName());
        member.setPhone(memberDTO.getPhone());
       // member.setAddress(memberDTO.getAddress());
        member.setCompanyName(memberDTO.getCompanyName());
        member.setEmail(memberDTO.getEmail());

        // @Transactional이 붙어있으므로 별도의 save 없이도 DB에 반영됩니다.
    }

    // 비밀번호 재설정
    @Transactional
    public void updatePassword(Long mid, String newPassword) {
        //1. 회원조회
        MemberEntity member = memberRepository.findById(mid)
                        .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        //2. 비밀번호 변경
        member.setPassword(newPassword);
    }

    // 회원 탈퇴
    public void deleteMember(Long mid) {
        memberRepository.deleteById(mid);
    }
}//end
