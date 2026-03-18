package com.example.stock.Service;

import com.example.stock.DTO.ProductDTO;
import com.example.stock.Entity.MemberEntity;
import com.example.stock.Entity.ProductEntity;
import com.example.stock.Repository.ProductRepository;
import com.example.stock.Util.FileUpload;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.example.stock.Entity.ProductImage; // 이 부분이 반드시 있어야 합니다.

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final FileUpload fileUpload;

    // 사용자(판매자)가 올린 모든 상품목록
    public List<ProductDTO> findAll() {
        // 1. DB에서 모든 엔티티를 가져옵니다.
        List<ProductEntity> productEntities = productRepository.findAll();

        // 2. 엔티티 리스트를 DTO 리스트로 변환하여 반환합니다.
        return productEntities.stream()
                .map(entity -> modelMapper.map(entity, ProductDTO.class))
                .collect(Collectors.toList());
    }

    //페이징처리, 상품검색, 구매자용
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductList(Pageable pageable, String pName){
        //JPA의 Page기능을 사용하여 페이징 처리된 엔티티 조회
        Page<ProductEntity> result = productRepository.findByPnameContaining(pName, pageable);

        //Entity를 DTO로 변환하여 반환
        return result.map(entity -> modelMapper.map(entity, ProductDTO.class));
    }

    @Value("${com.example.upload.path}")
    private String uploadPath;

    //상품등록(다중 이미지 처리)
    public Long registerProductWithImages(ProductDTO productDTO, List<MultipartFile> imgFiles){
        ProductEntity productEntity = modelMapper.map(productDTO, ProductEntity.class);

        // 💡 1. 시작할 때 기본 이미지를 먼저 설정합니다.
        productEntity.setImg("no_image.png");

        // 2. 파일 저장 및 이미지 엔티티 연결
        if (imgFiles != null && !imgFiles.isEmpty()) {
            int uploadCount = 0; //실제 업로드된 파일 수를 체크하기 위한 변수

            for (MultipartFile file : imgFiles) {
                if (file != null && !file.isEmpty()) {
                    String uuid = UUID.randomUUID().toString();
                    String originalName = file.getOriginalFilename();
                    String saveName = uuid + "_" + originalName;

                    try {
                        // C:\\upload 폴더에 실제 파일 저장
                        file.transferTo(new File(uploadPath, saveName));

                        // 3. ProductImage 생성 및 연관관계 설정
                        ProductImage productImage = ProductImage.builder()
                                .uuid(uuid)
                                .imgName(originalName)
                                .product(productEntity)
                                .build();
                        productEntity.addImage(productImage); // 연관관계 편의 메서드 호출

                        // 💡 2. 루프 인덱스(i) 대신, 실제 파일이 저장된 순서(uploadCount)로 대표 이미지를 설정합니다.
                        // 사용자가 1번 칸은 비우고 2번 칸만 채웠을 경우를 대비합니다.
                        if (uploadCount == 0) {
                            productEntity.setImg(saveName);
                        }
                        uploadCount++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // 최종 저장
        return productRepository.save(productEntity).getPid();

    }

    //상품 상세조회
    @Transactional(readOnly = true)
    public ProductDTO readProduct(Long pid){
        ProductEntity product = productRepository.findById(pid)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. "));
        return modelMapper.map(product, ProductDTO.class);
    }

    //상품수정
    @Transactional
    public void updateProduct(ProductDTO productDTO, List<MultipartFile> imgFiles, String removedFiles){
        // 기존 상품 엔티티 조회
        ProductEntity productEntity = productRepository.findById(productDTO.getPid())
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        // 1. 정보업데이트
        productEntity.setPname(productDTO.getPname());
        productEntity.setManufacturer(productDTO.getManufacturer());
        productEntity.setCategory(productDTO.getCategory());
        productEntity.setPrice(productDTO.getPrice());
        productEntity.setStock(productDTO.getStock());
        productEntity.setUnit(productDTO.getUnit());
        productEntity.setContent(productDTO.getContent());

        // 2. 부분삭제
        if (removedFiles != null && !removedFiles.trim().isEmpty()) {
            for (String fileName : removedFiles.split(",")) {
                if (fileName.isEmpty()) continue;
                fileUpload.deleteFile(fileName); // 실제 파일 삭제
                productEntity.getImageList().removeIf(img ->
                        (img.getUuid() + "_" + img.getImgName()).equals(fileName));
            }
        }

        // 3. 새 이미지 추가
        if (imgFiles != null) {
            for (MultipartFile file : imgFiles) {
                if (!file.isEmpty()) {
                    String uuid = UUID.randomUUID().toString();
                    String saveName = uuid + "_" + file.getOriginalFilename();
                    try {
                        file.transferTo(new File(uploadPath, saveName));
                        ProductImage img = ProductImage.builder()
                                .uuid(uuid).imgName(file.getOriginalFilename())
                                .product(productEntity).build();
                        productEntity.addImage(img);
                    } catch (IOException e) { e.printStackTrace(); }
                }
            }
        }

        // [4] 최종 이미지 상태 점검 (핵심!)
        // 이미지가 하나라도 있으면 첫 번째 이미지를 대표 이미지(img)로 설정
        if (!productEntity.getImageList().isEmpty()) {
            // 리스트에 이미지가 하나라도 남아있다면, 첫 번째 이미지를 대표 이미지로 설정
            ProductImage firstImg = productEntity.getImageList().get(0);
            productEntity.setImg(firstImg.getUuid() + "_" + firstImg.getImgName());
        } else {
            // 이미지가 하나도 없으면 기본 이미지로 설정
            productEntity.setImg("no_image.png");
        }
    }

    //상품 삭제
    public void deleteProduct(Long pid){
        ProductEntity product = productRepository.findById(pid)
                        .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. "));
        //서버에서 이미지 파일 삭제
        if(product.getImg() != null){
            fileUpload.deleteFile(product.getImg());
        }
        productRepository.deleteById(pid);
    }

    //판매자별 내 상품 리스트 조회
    @Transactional(readOnly = true)
    public List<ProductDTO> getMyProducts(MemberEntity seller){
        List<ProductEntity> products = productRepository.findBySeller(seller);
        return products.stream()
                .map(entity -> modelMapper.map(entity, ProductDTO.class))
                .collect(Collectors.toList());
    }
}
