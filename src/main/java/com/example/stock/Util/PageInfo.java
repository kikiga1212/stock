package com.example.stock.Util;

import com.example.stock.DTO.PageInfoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PageInfo {
    @Value("10")
    int pageLimit;  //한 화면에 보여줄 페이지 번호의 개수

    public PageInfoDTO getPageInfo(Page<?> page){
        int totalPages = page.getTotalPages();
        long totalRecords = page.getTotalElements();

        //유효성 검사
        if(totalPages == 0){
            return new PageInfoDTO(0,0,0,0,0,0,0);
        }

        int currentPage = page.getNumber() + 1;
        int halfPage = pageLimit / 2;
        int startPage = Math.max(currentPage - halfPage, 1);
        int endPage = Math.min(startPage + pageLimit - 1, totalPages);

        int pre = Math.max(currentPage - pageLimit, 1);
        int next = Math.min(currentPage + pageLimit, totalPages);
        int last = totalPages;

        return new PageInfoDTO(startPage,endPage,pre,currentPage,next,last,totalRecords);
    }
}
