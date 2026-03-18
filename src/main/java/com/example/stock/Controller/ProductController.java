package com.example.stock.Controller;

import com.example.stock.DTO.MemberDTO;
import com.example.stock.DTO.PageInfoDTO;
import com.example.stock.DTO.ProductDTO;
import com.example.stock.Entity.MemberEntity;
import com.example.stock.Service.ProductService;
import com.example.stock.Util.FileUpload;
import com.example.stock.Util.PageInfo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ModelMapper modelMapper;
    private final PageInfo pageInfo;
    private final FileUpload fileUpload;



    //상품 등록 페이지
    @GetMapping("/register")
    public String registerForm(){
        return "product/register";
    }

    //상품 등록처리
    @PostMapping("/register")
    public String registerProduct(
            ProductDTO productDTO,
            @RequestParam(value = "imgFiles", required = false) List<MultipartFile> imgFiles, // 여러 장 받기
            HttpSession session){

        // 세션에서 로그인 유저 정보 가져오기
        MemberDTO user = (MemberDTO) session.getAttribute("user");

        //로기인 안되어 있다면 로그인 페이지로 리다이렉트
        if(user == null) return "redirect:/member/login";

        // 1. 판매자 설정
        productDTO.setSeller(modelMapper.map(user, MemberEntity.class));

        //서비스 호출 (파일 저장 로직은 서비스의 registerProduct 안에 이미 있으므로 파일만 넘김)
        productService.registerProductWithImages(productDTO, imgFiles);
        return "redirect:/product/myList";
    }


    //상품 상세조회
    @GetMapping("/read/{pid}")
    public String readProduct(@PathVariable("pid") Long pid, Model model){
        ProductDTO productDTO = productService.readProduct(pid);
        model.addAttribute("product", productDTO);
        return "product/read";
    }

    // 상품 수정 페이지로 이동
    @GetMapping("/update/{pid}")
    public String updateForm(@PathVariable("pid") Long pid, Model model){
        ProductDTO productDTO = productService.readProduct(pid);
        model.addAttribute("product", productDTO);
        return "product/update";
    }

    //상품 수정 처리
    @PostMapping("/update")
    public String updateProduct(ProductDTO productDTO,
                                @RequestParam(value = "imgFiles", required = false) List<MultipartFile> imgFiles,
                                @RequestParam(value = "removedFiles", required = false) String removedFiles){

        // 사진을 선택하지 않았다면 productDTO.img는 null인 상태로 서비스로 넘어감
        productService.updateProduct(productDTO, imgFiles, removedFiles);
        return "redirect:/product/read/"+productDTO.getPid();
    }

    //상품 삭제 처리
    @PostMapping("/delete/{pid}")
    public String deleteProduct(@PathVariable("pid") Long pid){
        productService.deleteProduct(pid);
        return "redirect:/product/myList";
    }

    //판매자 본인의 상품 리스트
    @GetMapping("/myList")
    public String myList(HttpSession session, Model model){
        MemberDTO user = (MemberDTO) session.getAttribute("user");
        //로그인 안되어있으면  로그인페이지로 이동
        if( user == null ) return "redirect:/member/login";

        MemberEntity seller = modelMapper.map(user, MemberEntity.class);
        List<ProductDTO> myProducts = productService.getMyProducts(seller);
        model.addAttribute("products", myProducts);
        return "product/myList";
    }

    //상품 검색(구매자용)
    @GetMapping({"/","/search","/list"})
    public String getProductList(
            @RequestParam(value = "pname", required = false, defaultValue = "") String pname,//pname이 없어도 오류가 발생하지 않는다
            @RequestParam(value = "page", defaultValue = "1") int page,//페이지번호 파라미터 추가
            Model model){
        // 페이지번호가 1보다 작으면 1로 고정
        if(page < 1) page = 1;

        // 1. 페이지 요청 정보 생성 (한 페이지당 10개씩, 최신순 정렬)
        // 스프링 데이터 JPA의 페이지는 0부터 시작하므로 page - 1
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("pid").descending());

        //2. 서비스 호출(수정된 searchProducts는 이제 Page<ProductDTO>를 반환)
        Page<ProductDTO> result = productService.getProductList(pageable, pname);

        // 3. PageInfo 유틸리티를 사용하여 화면에 필요한 페이징 데이터(PageInfoDTO) 생성
        PageInfoDTO pageInfoDTO = pageInfo.getPageInfo(result);

        // 4. 모델에 데이터 담기
        model.addAttribute("products", result.getContent()); // 실제 상품 목록 (List<ProductDTO>)
        model.addAttribute("pageInfo", pageInfoDTO);         // 페이징 계산 결과
        model.addAttribute("pname", pname);                  // 검색어 유지용
        return "product/list";
    }

    @Value("${com.example.upload.path}") // application.properties의 경로와 일치해야 함
    private String uploadPath;
    // 이미지 출력 엔드포인트
    @GetMapping("/display")
     public ResponseEntity<Resource> display(@RequestParam("fileName") String fileName) {
        // 1. 경로 설정
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        // 2. 💡 만약 파일이 하드디스크에 없다면? (404 방지 로직)
        if (!resource.exists()) {
            // 프로젝트 내부의 static/images/no_image.png를 기본값으로 사용
            resource = new ClassPathResource("static/images/no_image.png");

            // 만약 static에도 없다면 에러 대신 빈 응답을 보냄
            if (!resource.exists()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        String resourceName = resource.getFilename();
        HttpHeaders header = new HttpHeaders();
        try {
            header.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(resource, header, HttpStatus.OK);
    }

}//end
