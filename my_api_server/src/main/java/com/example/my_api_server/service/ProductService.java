package com.example.my_api_server.service;

import com.example.my_api_server.entity.Product;
import com.example.my_api_server.repo.ProductRepo;
import com.example.my_api_server.service.dto.ProductCreateDto;
import com.example.my_api_server.service.dto.ProductResponseDto;
import com.example.my_api_server.service.dto.ProductUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductService {

    private final ProductRepo productRepo;

    // 상품 등록
    // JPA 하이버네이트는 DB랑 통신하기 위해, DB의 ACID가 되기 위해선 begin tran; commit 무조건 돼야 한다.

    @Transactional
    public ProductResponseDto createProduct(ProductCreateDto dto) {
        Product product = Product.builder()
                .productName(dto.getProductName())
                .productType(dto.getProductType())
                .productNumber(dto.getProductNumber())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build();

        Product savedProduct = productRepo.save(product);// 영속화

        // Entity -> DTO 변환
        ProductResponseDto resDto = ProductResponseDto.builder()
                .productNumber(savedProduct.getProductNumber())
                .stock(savedProduct.getStock())
                .price(savedProduct.getPrice())
                .build();

        return resDto;
    }

    // 상품 조회
    public ProductResponseDto findProduct(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow();

        ProductResponseDto resDto = ProductResponseDto.builder()
                .productNumber(product.getProductNumber())
                .stock(product.getStock())
                .price(product.getPrice())
                .build();

//        Product product2 = productRepo.findById(productId).orElseThrow();  // 1차캐싱했기 때문에 동일한 구문 있을 시 select 쿼리 x


        return resDto;
    }

    // 상품 수정

    @Transactional // dml을 실행하려면 무조건 있어야한다.
    public ProductResponseDto updateProduct(ProductUpdateDto dto) {
        // 1. 조회
        Product product = productRepo.findById(dto.productId()).orElseThrow();

        // 2. 필요한 것만 수정(상품명, 재고수량)
        product.changeProductName(dto.changeProductName());
        product.increaseStock(dto.changeStock());

        // 3. 리턴
        // Entity -> DTO 변환
        ProductResponseDto resDto = ProductResponseDto.builder()
                .productNumber(product.getProductNumber())
                .stock(product.getStock())
                .price(product.getPrice())
                .build();

        return resDto;
    }
}
