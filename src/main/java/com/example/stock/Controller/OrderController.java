package com.example.stock.Controller;

import com.example.stock.DTO.MemberDTO;
import com.example.stock.DTO.OrderDTO;
import com.example.stock.Entity.MemberEntity;
import com.example.stock.Service.OrderService;
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
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final ModelMapper modelMapper;

    //주문하기 처리
    @PostMapping("/create")
    public String createOrder(OrderDTO orderDTO, HttpSession session){
        MemberDTO user = (MemberDTO) session.getAttribute("user");
        orderDTO.setBuyer(modelMapper.map(user, MemberEntity.class));
        orderService.createOrder(orderDTO);
        return "redirect:/order/myOrders";
    }

    //나의 구매내역(구매자용)
    @GetMapping("/myOrders")
    public String myOrders(HttpSession session, Model model){
        MemberDTO user = (MemberDTO) session.getAttribute("user");
        List<OrderDTO> orders = orderService.getMyOrders(modelMapper.map(user, MemberEntity.class));
        model.addAttribute("orders", orders);
        return "order/myOrders";
    }

    // 들어온 주문 관리(판매자용)
    @GetMapping("/sellersOrders")
    public String sellersOrders(HttpSession session, Model model){
        MemberDTO user = (MemberDTO) session.getAttribute("user");
        List<OrderDTO> orders = orderService.getSellersOrders(modelMapper.map(user, MemberEntity.class));
        model.addAttribute("orders", orders);
        return "order/sellerOrders";
    }

    //주문상태변경(배송준비, 발송완료, 취소 등)
    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam("oid") Long oid,
                               @RequestParam("status") String status,
                               // 💡 type이 없을 경우를 대비해 기본값을 주거나 필수가 아님을 명시합니다.
                               @RequestParam(value = "type", required = false, defaultValue = "seller") String type){
        orderService.updateStatus(oid, status);
        //판매자 페이지에서 변경인지, 구매자페이지에서 변경했는지 따라 리다이렉트 분기
        return "seller".equals(type) ? "redirect:/order/sellersOrders" : "redirect:/order/myOrders";
    }
}//end
