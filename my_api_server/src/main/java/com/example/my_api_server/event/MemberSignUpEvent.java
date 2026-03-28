package com.example.my_api_server.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberSignUpEvent { // 이벤트 객체(리스너로 보낼 데이터)

    private final Long id;
    private final String email;

}
