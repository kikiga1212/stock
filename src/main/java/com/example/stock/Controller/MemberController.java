package com.example.stock.Controller;

import com.example.stock.DTO.MemberDTO;
import com.example.stock.Service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    //회원가입
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("member", new MemberDTO());
        return "member/signup";
    }

    @PostMapping("/signup")
    public String signup(MemberDTO memberDTO){
        try {
            memberService.signup(memberDTO);
            // 가입 성공 시 로그인 페이지로 이동 (URL이 /member/login으로 바뀜)
            return "redirect:/member/login";
        } catch (Exception e) {
            // 실패 시 다시 회원가입 페이지로 이동 (에러 메시지 포함 가능)
            return "redirect:/member/signup?error"+e.getMessage();
        }
    }

    //로그인
    @GetMapping("/login")
    public String loginForm(){
        return "member/login";
    }
    @PostMapping("/login")
    public String login(@RequestParam("businessNo") String businessNo,
                        @RequestParam("password") String password,
                        HttpSession session,
                        RedirectAttributes rttr){
        try{
            MemberDTO loginMember = memberService.login(businessNo, password);
            session.setAttribute("user", loginMember);
            return "redirect:/"; //메인페이지로 이동
        }catch (IllegalArgumentException e){
            // "비밀번호가 틀렸습니다" 등의 구체적인 메시지를 로그인 페이지로 보냄
            rttr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/member/login";
        }catch (Exception e) {
            rttr.addFlashAttribute("errorMessage", "로그인 중 알 수 없는 오류가 발생했습니다.");
            return "redirect:/member/login";
        }
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

    //마이페이지(내 정보 상세보기)
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model){
        MemberDTO user = (MemberDTO) session.getAttribute("user");
        if( user == null ) return "redirect:/member/login";

        model.addAttribute("member", user);
        return "member/mypage";
    }

    //내 정보 수정 처리
    @PostMapping("/update")
    public String update(MemberDTO memberDTO, HttpSession session){
        memberService.updateMember(memberDTO);
        //세션 정보 갱신
        session.setAttribute("user", memberDTO);
        return "redirect:/member/mypage?success";
    }

    //비밀번호 재설정
    @GetMapping("/password")
    public String passwordForm(){
        return "member/password";
    }
    @PostMapping("/password")
    public String updatePassword(@RequestParam("newPassword") String newPassword,
                                 HttpSession session){
        MemberDTO user = (MemberDTO) session.getAttribute("user");
        memberService.updatePassword(user.getMid(), newPassword);
        return "redirect:/member/mypage?pwSuccess";
    }

    //회원탈퇴
    @GetMapping("/signout")
    public String signout(HttpSession session){
        MemberDTO user = (MemberDTO) session.getAttribute("user");
        memberService.deleteMember(user.getMid());
        session.invalidate();
        return "redirect:/";
    }
}//end

