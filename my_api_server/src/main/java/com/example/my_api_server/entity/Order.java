package com.example.my_api_server.entity;

import com.example.my_api_server.service.dto.OrderResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 매개변수 전부 받는 생성자 생성
@Table(name = "orders")
@Getter
@Builder
public class Order {

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderProduct> orderProducts = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member buyer; // 구매자

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태

    @Column(nullable = false)
    private LocalDateTime orderTime; // 주문 시간

    // 주문(1) : 주문상품(N) 바지,신발,모자...  1:N 관계를 나타내야 한다.
    // 주문(1) <-> 주문상품들(N) <-> 상품(1)

    //정적 팩토리 패턴
    public static Order createOrder(Member member, LocalDateTime orderTime) {
        return Order.builder()
                .buyer(member)
                .orderStatus(OrderStatus.PENDING)
                .orderTime(orderTime)
                .build();
    }

    // 루트 엔티티(에그리거트 루트) 내부 응집도 상승
    public OrderProduct createOrderProduct(Long orderCount, Product product) {
        return OrderProduct.builder()
                .order(this)
                .number(orderCount) // 주문개수 매핑
                .product(product)
                .build();
    }

    // 양방향 매핑
    public void addOrderProducts(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public OrderResponseDto changeOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        return OrderResponseDto.of(orderTime, orderStatus, true);
    }

}
