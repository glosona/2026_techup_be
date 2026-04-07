package com.example.my_api_server.common;

import com.example.my_api_server.entity.Product;
import com.example.my_api_server.entity.ProductType;

import java.util.List;

public class ProductFixture {

    public static Product.ProductBuilder defaultProduct() {
        return Product.builder()
                .productType(ProductType.CLOTHES);
    }

    public static List<Product> defaultProducts() {
        Product product1 = Product.builder()
                .productNumber("TEST1")
                .productName("티셔츠 1")
                .productType(ProductType.CLOTHES)
                .price(1000L)
                .stock(1L)
                .build();

        Product product2 = Product.builder()
                .productNumber("TEST2")
                .productName("티셔츠 2")
                .productType(ProductType.CLOTHES)
                .price(2000L)
                .stock(2L)
                .build();

        return List.of(product1, product2);
    }
}

