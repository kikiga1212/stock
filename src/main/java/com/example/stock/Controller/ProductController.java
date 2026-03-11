package com.example.stock.Controller;

import com.example.stock.DTO.MemberDTO;
import com.example.stock.DTO.ProductDTO;
import com.example.stock.Entity.MemberEntity;
import com.example.stock.Service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ModelMapper modelMapper;

    //상품 등록 페이지
    @GetMapping("/register")
    public String registerForm(){
        return "product/register";
    }

    //상품 등록처리
    @PostMapping("/register")
    public String registerProduct(ProductDTO productDTO, HttpSession session){
        MemberDTO user = (MemberDTO) session.getAttribute("user");
        //로그인한 세션 유저를 판매자로 설정
        productDTO.setSeller(modelMapper.map(user, MemberEntity.class));
        productService.registerProduct(productDTO);
        return "redirect:/product/myList";
    }

    //판매자 본인의 상품 리스트
    @GetMapping("/myList")
    public String myList(HttpSession session, Model model){
        MemberDTO user = (MemberDTO) session.getAttribute("user");
        MemberEntity seller = modelMapper.map(user, MemberEntity.class);
        List<ProductDTO> myProducts = productService.getMyProducts(seller);
        model.addAttribute("products", myProducts);
        return "product/myList";
    }

    //상품 검색(구매자용)
    @GetMapping("/search")
    public String searchProducts(@RequestParam(value = "pName", required = false)//pName이 없어도 오류가 발생하지 않는다
                                     String pName, Model model){
        List<ProductDTO> searchProducts = productService.searchProducts(pName != null ? pName : "");
        model.addAttribute("products", searchProducts);
        return "product/list";
    }
}//end
