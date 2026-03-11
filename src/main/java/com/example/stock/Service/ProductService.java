package com.example.stock.Service;

import com.example.stock.DTO.ProductDTO;
import com.example.stock.Entity.MemberEntity;
import com.example.stock.Entity.ProductEntity;
import com.example.stock.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    //상품등록
    public Long registerProduct(ProductDTO productDTO){
        ProductEntity product = modelMapper.map(productDTO, ProductEntity.class);
        return productRepository.save(product).getPid();
    }

    //상품수정
    public void updateProduct(ProductDTO productDTO){
        ProductEntity product = productRepository.findById(productDTO.getPid())
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        //정보업데이트
        product.setPName(productDTO.getPName());
        product.setManufacturer(productDTO.getManufacturer());
        product.setCategory(productDTO.getCategory());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setUnit(productDTO.getUnit());
    }

    //상품 삭제
    public void deleteProduct(Long pid){
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

    // 상품검색(구매자용)
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String pName){
        List<ProductEntity> products = productRepository.findByPNameContaining(pName);
        return products.stream()
                .map(entity -> modelMapper.map(entity, ProductDTO.class))
                .collect(Collectors.toList());
    }
}
