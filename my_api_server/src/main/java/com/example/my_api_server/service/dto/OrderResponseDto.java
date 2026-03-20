package com.example.my_api_server.service.dto;

import com.example.my_api_server.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(staticName = "of")
@Builder
public class OrderResponseDto {

    private LocalDateTime orderCompleteTime; // 주문 완료 시간

    private OrderStatus orderStatus; // 주문 상태

    private boolean isSuccess; // 주문성공 여부
}
