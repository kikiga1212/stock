package com.example.stock.Service;

import com.example.stock.DTO.OrderDTO;
import com.example.stock.Entity.MemberEntity;
import com.example.stock.Entity.OrderEntity;
import com.example.stock.Entity.ProductEntity;
import com.example.stock.Repository.OrderRepository;
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
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    //주문생성(구매하기)
    public Long createOrder(OrderDTO orderDTO){
        ProductEntity product = productRepository.findById(orderDTO.getProduct().getPid())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. "));

        //재고 확인 및 차감
        if (product.getStock() < orderDTO.getOrderAmount()) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        product.setStock(product.getStock() - orderDTO.getOrderAmount());

        OrderEntity order = OrderEntity.builder()
                .buyer(orderDTO.getBuyer())
                .product(product)
                .orderAmount(orderDTO.getOrderAmount())
                .totalPrice(product.getPrice() * orderDTO.getOrderAmount())
                .status("결제대기") //초기상태 설정
                .build();
        return orderRepository.save(order).getOid();
    }

    //주문 상태 변경(판매자/구매자 공용)
    //배송준비, 발송완료, 반품요청, 취소 등 처리
    public void updateStatus(Long oid, String newStatus){
        OrderEntity order = orderRepository.findById(oid)
                .orElseThrow(() -> new IllegalArgumentException("주문내역을 찾을 수 없습니다. "));

        // 만약 '취소'상태로 변경시 재고를 다시 돌려주는 로직
        if("구매취소".equals(newStatus) || "거래취소".equals(newStatus)){
            ProductEntity product = order.getProduct();
            product.setStock(product.getStock() + order.getOrderAmount());
        }
        order.setStatus(newStatus);
    }

    //나의 구매내역 조회(구매자용)
    @Transactional(readOnly = true)
    public List<OrderDTO> getMyOrders(MemberEntity buyer){
        return orderRepository.findByBuyer(buyer).stream()
                .map(entity -> modelMapper.map(entity, OrderDTO.class))
                .collect(Collectors.toList());
    }

    //들어온 주문 내역 조회(판매자용)
    @Transactional(readOnly = true)
    public List<OrderDTO> getSellersOrders(MemberEntity seller){
        return orderRepository.findByProduct_Seller(seller).stream()
                .map(entity -> modelMapper.map(entity, OrderDTO.class))
                .collect(Collectors.toList());
    }
}// end
