package com.example.my_api_server.service;

import com.example.my_api_server.config.TestContainerConfig;
import com.example.my_api_server.entity.Member;
import com.example.my_api_server.entity.Product;
import com.example.my_api_server.entity.ProductType;
import com.example.my_api_server.repo.MemberDBRepo;
import com.example.my_api_server.repo.OrderProductRepo;
import com.example.my_api_server.repo.OrderRepo;
import com.example.my_api_server.repo.ProductRepo;
import com.example.my_api_server.service.dto.OrderCreateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestContainerConfig.class)
class OrderServiceTest2 {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceTest2.class);

    @Autowired
    OrderService orderService;


    @Autowired
    private OrderRepo orderRepository;

    @Autowired
    private OrderProductRepo orderProductRepo;

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private MemberDBRepo memberRepository;


    private Member saveMember() {
        return memberRepository.save(Member.builder()
                .email("test1@gmail.com")
                .password("1234")
                .build()
        );
    }

    private List<Product> saveProducts(long stock) {
        Product product1 = Product.builder()
                .stock(stock)
                .productName("상품A")
                .price(10000L)
                .productType(ProductType.CLOTHES)
                .productNumber("TEST-001")
                .build();

        Product product2 = Product.builder()
                .stock(stock)
                .productName("상품B")
                .price(20000L)
                .productType(ProductType.ACCESSORIES)
                .productNumber("TEST-002")
                .build();

        Product product3 = Product.builder()
                .stock(stock)
                .productName("상품C")
                .price(30000L)
                .productType(ProductType.FOOD)
                .productNumber("TEST-003")
                .build();

        return productRepository.saveAll(List.of(product1, product2, product3));
    }

    @BeforeEach
    public void setUp() {
        orderProductRepo.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }


    /**
     * @concurrencyOrderUsers : 동시 요청 유저 수
     * @stock : 재고 수
     * @quantity : 주문 개수
     */
    @ParameterizedTest
    @CsvSource({
            "1, 1, 1",
            "2, 2, 1",
            "5, 5, 1",
            "5, 3, 2",
            "10, 8, 2",
            "20, 10, 2",
            "50, 25, 3"
    })
    @DisplayName("상품 재고 동시성 테스트(의도적인 race condition 상황 생성)")
    void createOrderConcurrencyTest(int concurrencyOrderUsers, int stock, long quantity)
            throws Exception {
        //given
        Member member = saveMember();
        List<Product> saveProducts = saveProducts(stock);

        OrderCreateDto request = new OrderCreateDto(
                member.getId(),
                saveProducts.stream().map(Product::getId).toList(), // 상품 ID들
                List.of(quantity, quantity, quantity) // 각 상품의 수량들
        );

        ExecutorService executor = Executors.newFixedThreadPool(concurrencyOrderUsers);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(concurrencyOrderUsers);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        //동시에 유저 N명이 주문할경우(케이스)
        for (int i = 0; i < concurrencyOrderUsers; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); //모든 스레드 여기서 대기 후
//                    orderService.createOrderOptLock(request); //로직 실행(race-condition 발생)
                    orderService.createOrderPLock(request); //로직 실행(race-condition 발생)
                    successCount.incrementAndGet(); //주문 성공 카운트
                } catch (Exception e) {
                    failCount.incrementAndGet(); //실패 처리 카운트
                } finally {
                    doneLatch.countDown(); //완료 처리
                }
            });
        }

        //when
        startLatch.countDown(); //동시 실행 시작
        doneLatch.await(); // 모든 작업 종료 대기
        executor.shutdown(); //executor 종료

        // then
        List<Long> productIds = request.productId();
        List<Product> products = productRepository.findAllById(productIds);
        long orderCount = orderRepository.count();

        int expectedSuccess = concurrencyOrderUsers;
        for (Long q : request.count()) {
            expectedSuccess = Math.min(expectedSuccess, stock / q.intValue());
        }

        // 상품별 검증
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            Long orderQty = request.count().get(i);

            int expectedRemaining = stock - (expectedSuccess * orderQty.intValue()); //예상 잔여 재고

            assertThat(p.getStock()).isEqualTo(expectedRemaining); //현재 재고랑 잔여 재고 같은지 확인
        }

        assertThat(orderCount).isEqualTo(expectedSuccess); // 주문 수 검증
    }
}