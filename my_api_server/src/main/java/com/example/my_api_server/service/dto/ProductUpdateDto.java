package com.example.my_api_server.service.dto;

// 상품 id > 상품명, 재고수량 변경가능
public record ProductUpdateDto(
        Long productId,
        String changeProductName,
        Long changeStock
) {
}
