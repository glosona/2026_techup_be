package com.example.my_api_server.service.dto;

import com.example.my_api_server.entity.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductCreateDto {

    private String productName; // 상품명

    private String productNumber; // 상품번호

    private ProductType productType; // 상품타입

    private Long price; // 가격

    private Long stock; // 재고
}
