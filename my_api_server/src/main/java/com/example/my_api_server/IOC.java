package com.example.my_api_server;

import org.springframework.stereotype.Component;

// 실제 스프링에게 빈(객체)으로 등록하게 해주는 설정
// IoC 컨테이너에 등록됨. (객체는 단 하나만 생성됨 -> 싱글톤 패턴)
@Component
public class IOC {

    public void func1() {
        System.out.println("func1 실행");
    }

    static void main(String[] args) {

    }
}
