package com.example.stock.DTO;

import com.example.stock.Constant.Role;
import lombok.*;

@Getter
@Setter
@ToString @Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
    private Long mid;
    private String businessNo; //사업자등록번호
    private String name;        // 대표자명
    private String password;    //비밀번호
    private String companyName;//사업자명
    private String email;       // 이메일
    private String phone;       //전화번호
    private String address;     //주소
    private Role role;          //사용자권한(USER,ADMIN)
}
