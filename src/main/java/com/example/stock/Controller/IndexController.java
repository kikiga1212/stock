package com.example.stock.Controller;

import com.example.stock.DTO.ProductDTO;
import com.example.stock.Service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {
    private final ProductService productService;

    public IndexController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String index(Model model){
        //DB에서 모든 상품 목록을 가져와서 모델에 담습니다.
        List<ProductDTO> productList = productService.findAll();
        model.addAttribute("productList", productList);
        return "index";
    }
}
