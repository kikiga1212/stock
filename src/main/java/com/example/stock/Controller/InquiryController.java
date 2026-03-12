package com.example.stock.Controller;

import com.example.stock.DTO.InquiryDTO;
import com.example.stock.DTO.MemberDTO;
import com.example.stock.Entity.MemberEntity;
import com.example.stock.Service.InquiryService;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/inquiry")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;
    private final ModelMapper modelMapper;

    // 1:1 상담 등록 페이지
    @GetMapping("/register")
    public String registerForm(){
        return "inquiry/register";
    }

    //상담 등록 처리
    @PostMapping("/register")
    public String registerInquiry(InquiryDTO inquiryDTO, HttpSession session){
        MemberDTO user = (MemberDTO) session.getAttribute("user");

        //작성자 정보 설정
        inquiryDTO.setWriter(modelMapper.map(user, MemberEntity.class));
        inquiryService.registerInquiry(inquiryDTO);
        return "redirect:/inquiry/list";
    }

    //나의 상담 내역 조회
    @GetMapping("/list")
    public String myList(HttpSession session, Model model){
        MemberDTO user = (MemberDTO) session.getAttribute("user");
        MemberEntity writer = modelMapper.map(user, MemberEntity.class);

        List<InquiryDTO> inquiries = inquiryService.getMyInquiries(writer);
        model.addAttribute("inquiries", inquiries);
        return "inquiry/list";
    }

    //상담 상세보기(답변 확인용)
    @GetMapping("/read/{iid}")
    public String read(@PathVariable("iid") Long iid, Model model){
        InquiryDTO inquiryDTO = inquiryService.getInquiry(iid);
        model.addAttribute("inquiry", inquiryDTO);
        return "inquiry/read";
    }
}
