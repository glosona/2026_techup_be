package com.example.my_api_server.service;

import com.example.my_api_server.entity.*;
import com.example.my_api_server.repo.MemberDBRepo;
import com.example.my_api_server.repo.OrderRepo;
import com.example.my_api_server.repo.ProductRepo;
import com.example.my_api_server.service.dto.OrderCreateDto;
import com.example.my_api_server.service.dto.OrderResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * OrderService 단위테스트
 */

@ExtendWith(MockitoExtension.class) // Mockito 활성화
class OrderServiceUnitTest {

    @Mock // 가짜 객체 생성
    ProductRepo productRepo;

    @Mock // 가짜 객체 생성
    MemberDBRepo memberDBRepo;

    @Mock // 가짜 객체 생성
    OrderRepo orderRepo;

    @InjectMocks // 실제 테스트할 대상 클래스(Mock 객체를 자동으로 주입받는다)
    OrderService orderService;

    @Test
    @DisplayName("Text")
    public void test1() {
        // given(when절에 필요한 데이터를 생성)
        int a = 10;


        // when(실제 수행할 메서드)
        a++;


        // then(테스트 결과를 확인
        assertThat(a).isEqualTo(11);

    }

    // tc1
    @Test
    @DisplayName("[HAPPY] 주문 요청이 정상적으로 잘 등록된다.")
    public void createOrderSuccess() {
        //given
        Long memberId = 1L;
        List<Long> productIds = List.of(1L, 2L);
        List<Long> counts = List.of(1L, 2L);

        Product product1 = Product.builder()
                .productNumber("TEST1")
                .productName("티셔츠")
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

        Member member = Member.builder()
                .email("tes1@test.com")
                .password("1234")
                .build();

        OrderCreateDto createDto = new OrderCreateDto(memberId, productIds, counts);

        when(productRepo.findAllById(productIds)).thenReturn(List.of(product1, product2));
        when(memberDBRepo.findById(memberId)).thenReturn(Optional.of(member));
        when(orderRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        //when(테스트할 메서드)
        OrderResponseDto dto = orderService.createOrder(createDto);


        //then(값 검증)
        ArgumentCaptor<Order> capture = ArgumentCaptor.forClass(Order.class);
        verify(orderRepo).save(capture.capture()); // orderRepo save()가 호출되는지 확인

        assertThat(dto.isSuccess()).isTrue();
        assertThat(dto.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);

    }


    @Test
    @DisplayName("[EXCEPTION] 주문 요청시 재고 부족하면 예외 처리가 정상 동작한다")
    public void productStockValid() {
        //given
        Long memberId = 1L;
        List<Long> productIds = List.of(1L, 2L);
        List<Long> counts = List.of(10L, 20L);

        Product product1 = Product.builder()
                .productNumber("TEST1")
                .productName("티셔츠")
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

        Member member = Member.builder()
                .email("tes1@test.com")
                .password("1234")
                .build();

        OrderCreateDto createDto = new OrderCreateDto(memberId, productIds, counts);

        when(productRepo.findAllById(productIds)).thenReturn(List.of(product1, product2));
        when(memberDBRepo.findById(memberId)).thenReturn(Optional.of(member));

        //when(테스트할 메서드)


//        then(값 검증)
        assertThatThrownBy(() -> orderService.createOrder(createDto))
                .isInstanceOf(RuntimeException.class)// 해당 예외 클래스가 어떤 건지 지정
                .hasMessage("재고가 음수이니 주문 할 수 없습니다!"); // 해당 예외 메시지가 어떤 건지 지정
    }

    //    @Test
    @DisplayName("[EXCEPTION] 주문 시간 날짜 오류 테스트")
    public void orderTimeException() {
        //given
        Long memberId = 1L;
        List<Long> productIds = List.of(1L, 2L);
        List<Long> counts = List.of(1L, 2L);

        Product product1 = Product.builder()
                .productNumber("TEST1")
                .productName("티셔츠")
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

        Member member = Member.builder()
                .email("tes1@test.com")
                .password("1234")
                .build();

        OrderCreateDto createDto = new OrderCreateDto(memberId, productIds, counts);

        when(productRepo.findAllById(productIds)).thenReturn(List.of(product1, product2));
        when(memberDBRepo.findById(memberId)).thenReturn(Optional.of(member));

        //when(테스트할 메서드)
        OrderResponseDto dto = orderService.createOrder(createDto);

//        then(값 검증)
        assertThat(dto).isNotNull();
    }

}