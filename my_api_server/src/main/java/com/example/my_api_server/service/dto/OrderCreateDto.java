package com.example.my_api_server.service.dto;

import java.util.List;

public record OrderCreateDto(
        Long memberId, // 구매자

        List<Long> productId, // 주문 상품 IDs

        List<Long> count // 주문 수량


) {

}
