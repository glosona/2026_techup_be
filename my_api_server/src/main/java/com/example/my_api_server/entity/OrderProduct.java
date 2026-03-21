package com.example.my_api_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "order_products")
@Getter
@Builder
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주문(1) <-> 주문상품(여러 상품들) <-> 상품(1)
    @ManyToOne(fetch = FetchType.LAZY) // FK
    private Product product; // 상품

    @ManyToOne(fetch = FetchType.LAZY) // FK
    private Order order; // 주문

    private Long number; // 주문 수량
}
