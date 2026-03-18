package com.example.stock.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageDTO {
    private Long inum;
    private String uuid;
    private String imgName;
}