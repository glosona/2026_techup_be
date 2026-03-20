package com.example.my_api_server.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private String productNumber; // 상품번호

    private Long price; // 가격

    private Long stock; // 재고
}
