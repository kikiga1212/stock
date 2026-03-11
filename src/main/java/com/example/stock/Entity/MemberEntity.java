package com.example.stock.Entity;

import com.example.stock.Constant.Role;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "member")
@Getter @Setter
@ToString
@Builder
@AllArgsConstructor @NoArgsConstructor
public class MemberEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mid;           //번호
    @Column(length = 20, unique = true, nullable = false)
    private String businessNo; //사업자등록번호
    @Column(length = 50, nullable = false)
    private String name;        // 대표자명
    @Column(length = 300, nullable = false)
    private String password;    //비밀번호
    @Column(length = 100, nullable = false)
    private String companyName;//사업자명
    @Column(length = 100, nullable = false)
    private String email;       // 이메일
    @Column(length = 20, nullable = false)
    private String phone;       //전화번호
    @Column(length = 200, nullable = false)
    private String address;     //주소
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;          //사용자권한(USER,ADMIN)
}
