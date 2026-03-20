package com.example.my_api_server.service;

import com.example.my_api_server.entity.*;
import com.example.my_api_server.repo.MemberDBRepo;
import com.example.my_api_server.repo.OrderRepo;
import com.example.my_api_server.repo.ProductRepo;
import com.example.my_api_server.service.dto.OrderCreateDto;
import com.example.my_api_server.service.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepo orderRepo;
    private final MemberDBRepo memberDBRepo;
    private final ProductRepo productRepo;

    // 주문 생성
    @Transactional
    public OrderResponseDto createOrder(OrderCreateDto dto) {

        Member member = memberDBRepo.findById(dto.memberId()).orElseThrow();

        LocalDateTime orderTime = LocalDateTime.now();

        Order order = Order.builder()
                .buyer(member)
                .orderStatus(OrderStatus.PENDING)
                .orderTime(orderTime)
                .build();


        // 1. OrderProduct에 저장하는 로직
        //   - 1주문에 여러 상품(N)

        // 상품 id들을 통해서 상품들을 조회
        List<Product> products = dto.productId().stream()
                .map(pId -> productRepo.findById(pId).orElseThrow())
                .toList();

        // Product <-> 주문개수랑 매핑
        HashMap<Product, Long> productCountMap = new HashMap<>();
        for (int i = 0; i < dto.count().size(); i++) {
            productCountMap.put(products.get(i), dto.count().get(i));
        }

        // 상품 찾았으니, OrderProduct를 만들자.
        List<OrderProduct> orderProducts = products.stream()
                .map((p) -> OrderProduct.builder()
                        .order(order)
                        .number(productCountMap.get(p)) // product에 맞는 주문개수를 찾는다.
                        .product(p)
                        .build()
                ).toList();

        // OrderProduct 생명주기 생성 완료
        order.addOrderProducts(orderProducts);

        // 2. 재고 차감

        products.stream()
                .forEach(p -> p.decreaseStock(productCountMap.get(p)));

        // order save를 하기 전에는 영속화 x
        // 영속화를 통해서 업데이트를 쉽게 할 수 있는 더티체크 가능
        // DB에서 계속 조회하지 않고, 내부에서 빠르게 조회할 수 있는 캐시 매커니즘
        Order savedOrder = orderRepo.save(order);
        // order save를 한 후에는 영속화 진행 (자식으로서 관리를 하겠다.)

        // Entity -> Dto로 변환
        OrderResponseDto orderResponseDto = OrderResponseDto.of(
                savedOrder.getOrderTime(),
                OrderStatus.COMPLETED,
                true);


        return orderResponseDto;
    }

    /**
     * JPA는 내부적으로  캐시 매커니즘
     * - 내부에 1차캐시, 2차캐시
     * - 1차캐시 내부적으로 영속화(내 자식으로 만들겠다.)
     * - 1차캐시에 데이터가 있으면 DB체크 X,
     * - 1차캐시에 데이터가 있으면 바로 반환.
     * - readOnly = true 시 내부 하이버네이트 동작원리가 간소화된다.(더티체크 X)
     * <p>
     * 검색, 조회하는 부분에서 readOnly true를 많이한다.
     * 근데, 사실 readOnly true도 더티체킹을 하긴 한다(?)
     */

    // 주문 조회
    @Transactional(readOnly = true)
    public OrderResponseDto findOrder(Long orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow(); // ID로 주문 조회

        OrderResponseDto orderResponseDto = OrderResponseDto.of(
                order.getOrderTime(),
                order.getOrderStatus(),
                true);

        return orderResponseDto;

    }
}
