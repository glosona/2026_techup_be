package com.example.my_api_server.controller;

import com.example.my_api_server.service.OrderService;
import com.example.my_api_server.service.dto.OrderCreateDto;
import com.example.my_api_server.service.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


// 반복적으로 사용하는 문법이나 코드들 = 보일러 플레이트
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponseDto createOrder(@Validated @RequestBody OrderCreateDto dto) {
        return orderService.createOrder(dto);
    }

    @GetMapping("/{id}")
    public OrderResponseDto findOrder(@PathVariable Long id) {
        return orderService.findOrder(id);
    }

    
}
