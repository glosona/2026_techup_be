package com.example.my_api_server.service;

import com.example.my_api_server.entity.*;
import com.example.my_api_server.repo.MemberDBRepo;
import com.example.my_api_server.repo.OrderRepo;
import com.example.my_api_server.repo.ProductRepo;
import com.example.my_api_server.service.dto.OrderCreateDto;
import com.example.my_api_server.service.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

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

        Member member = memberDBRepo.findById(dto.memberId())
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
//      기본값:  throw new NoSuchElementException("No value present");

        /**
         * 1. OrderProduct에 저장
         *   - 주문(1)에 여러 상품(N)
         *   - 리팩토링 이전: 상품 id들을 통해서 상품들을 조회, 각각 N번의 쿼리 조회
         *   - 리팩토링 이후: IN 쿼리로 쿼리를 1번만, IntStream으로 주문개수 매핑
         *
         * 2. 재고 차감
         *   - 리팩토링 이전: N번 돌면서 매핑된 값으로 차감
         *   - 리팩토링 이후: IntStream 내에서 해결
         */
//        List<Product> products = dto.productId().stream()
//                .map(pId -> productRepo.findById(pId).orElseThrow())
//                .toList();
//
//        HashMap<Product, Long> productCountMap = new HashMap<>();
//        for (int i = 0; i < dto.count().size(); i++) {
//            productCountMap.put(products.get(i), dto.count().get(i));
//        }
//
//        List<OrderProduct> orderProducts = products.stream()
//                .map((p) -> OrderProduct.builder()
//                        .order(order)
//                        .number(productCountMap.get(p)) // product에 맞는 주문개수를 찾는다.
//                        .product(p)
//                        .build()
//                ).toList();

        Order order = Order.createOrder(member, dto.orderTime());
        List<Product> products = productRepo.findAllById(dto.productId()); // IN 쿼리

        List<OrderProduct> orderProducts = IntStream.range(0, dto.count().size())
                .mapToObj(idx -> {
                    // 재고 차감
                    Product product = products.get(idx);
                    Long orderCount = dto.count().get(idx);

                    product.buyProductWithStock(orderCount);

                    return order.createOrderProduct(orderCount, product);
                })
                .toList();

        order.addOrderProducts(orderProducts);

        // save()를 하기 전에는 영속화 x
        // - 영속화를 통해서 업데이트를 쉽게 할 수 있는 더티체크 가능
        // - DB에서 계속 조회하지 않고, 내부에서 빠르게 조회할 수 있는 캐시 매커니즘

        Order savedOrder = orderRepo.save(order);
        // save() 한 후에는 영속화 진행 (자식으로서 관리를 하겠다.)

        // Entity -> Dto로 변환
        return OrderResponseDto.of(
                savedOrder.getOrderTime(),
                OrderStatus.COMPLETED,
                true);
    }

    // 비관적 락 사용 예시
    @Transactional
    public OrderResponseDto createOrderPLock(OrderCreateDto dto) {
        Member member = memberDBRepo.findById(dto.memberId()).orElseThrow();
        LocalDateTime orderTime = LocalDateTime.now(); //2026-04-03T12:16:19.363524

        if (orderTime.getHour() == 13) {
            // 로직 실행(점심시간 이벤트 쿠폰 발행)
            return null;
        }

        Order order = Order.builder()
                .buyer(member)
                .orderStatus(OrderStatus.PENDING)
                .orderTime(orderTime)
                .build();

        List<Product> products = productRepo.findAllByIdsWithXLock(dto.productId()); // FOR NO UPDATE LOCK(배타락)

        List<OrderProduct> orderProducts = IntStream.range(0, dto.count().size())
                .mapToObj(idx -> {
                    Product product = products.get(idx);

                    if (product.getStock() - dto.count().get(idx) < 0) {
                        throw new RuntimeException("재고가 음수이니 주문 할 수 없습니다!");
                    }

                    product.decreaseStock(dto.count().get(idx));

                    return OrderProduct.builder()
                            .order(order)
                            .number(dto.count().get(idx))
                            .product(products.get(idx))
                            .build();
                })
                .toList();

        order.addOrderProducts(orderProducts);
        Order savedOrder = orderRepo.save(order);

        OrderResponseDto orderResponseDto = OrderResponseDto.of(
                savedOrder.getOrderTime(),
                OrderStatus.COMPLETED,
                true);

        return orderResponseDto;
    }

    // 낙관적 락 사용 예시
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(includes = ObjectOptimisticLockingFailureException.class, maxRetries = 3)
    public OrderResponseDto createOrderOptLock(OrderCreateDto dto) {
        log.info("@Retryable 테스트");
        Member member = memberDBRepo.findById(dto.memberId()).orElseThrow();
        LocalDateTime orderTime = LocalDateTime.now();
        Order order = Order.builder()
                .buyer(member)
                .orderStatus(OrderStatus.PENDING)
                .orderTime(orderTime)
                .build();

        List<Product> products = productRepo.findAllById(dto.productId());

        List<OrderProduct> orderProducts = IntStream.range(0, dto.count().size())
                .mapToObj(idx -> {
                    Product product = products.get(idx);

                    if (product.getStock() - dto.count().get(idx) < 0) {
                        throw new RuntimeException("재고가 음수이니 주문 할 수 없습니다!");
                    }

                    product.decreaseStock(dto.count().get(idx));

                    return OrderProduct.builder()
                            .order(order)
                            .number(dto.count().get(idx))
                            .product(products.get(idx))
                            .build();
                })
                .toList();

        order.addOrderProducts(orderProducts);
        Order savedOrder = orderRepo.save(order);
        OrderResponseDto orderResponseDto = OrderResponseDto.of(
                savedOrder.getOrderTime(),
                OrderStatus.COMPLETED,
                true);

        return orderResponseDto;
    }


    // 주문 확정
    @Transactional
    public OrderResponseDto completeOrder(Long orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow();

        OrderResponseDto completedOrder = order.changeOrderStatus(OrderStatus.COMPLETED);
        return completedOrder;
    }


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
