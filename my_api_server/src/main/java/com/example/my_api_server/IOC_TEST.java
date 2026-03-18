package com.example.my_api_server;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 컨트롤러로 등록
@RequestMapping("/test")
@RequiredArgsConstructor // DI를 자동으로 해주는 어노테이션(생성자 주입방식을 어노테이션으로 사용하는 방법)
public class IOC_TEST {

    // 1. 필드 주입(잘 안쓴다)
//    @Autowired
//    private IOC ioc2;
//
//    // 2. Setter(수정자) 주입 방식(잘 안쓴다)
//    public IOC setIoc(IOC ioc) {
//        ioc2 = ioc;
//        return ioc2;
//    }
//
//    @Autowired
//    public IOC setIoc2(IOC ioc) {
//        ioc2 = ioc;
//        return ioc2;
//    }
//
//
//    // 3. 생성자 주입방식(생성할 때 자동으로 주입받음, 주로 많이 씀)
//    public void IOC(IOC ioc) {
//        ioc2=ioc;
//    }

    // 각 3가지 방법으로 객체를 주입받음
    // 이걸 간단하게 어노테이션으로 한 게 > @RequiredArgsConstructor (대부분 이걸 씀)


    // final: 불변성. 객체를 변경할 수 없음
    private final IOC ioc;


    @GetMapping
    public void iocTest() {
        ioc.func1();
    }
}
