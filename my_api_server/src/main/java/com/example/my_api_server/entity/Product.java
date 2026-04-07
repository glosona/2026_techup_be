package com.example.my_api_server.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@Getter
@Builder
public class Product { // 상품

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // pk

    private String productName; // 상품명

    private String productNumber; // 상품번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType; // 상품타입

    private Long price; // 가격

    private Long stock; // 재고

    @Version
    private Long version;

    // 필요한 것만 바꾸게
    public void changeProductName(String changeProductName) {
        this.productName = changeProductName;
    }

    // 재고 +
    public void increaseStock(Long addStock) {
        this.stock += addStock;
    }

    // 재고 -
    public void decreaseStock(Long subStock) {
        this.stock -= subStock;
    }

    public void buyProductWithStock(Long orderCount) {
        if (this.getStock() - orderCount < 0) {
            throw new RuntimeException("재고가 음수이니 주문 할 수 없습니다.");
        }
        this.decreaseStock(orderCount);

    }

}
