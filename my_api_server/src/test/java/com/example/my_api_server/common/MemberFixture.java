package com.example.my_api_server.common;

import com.example.my_api_server.entity.Member;

// 공통으로 사용하는 멤버를 생성해주는 클래스
public class MemberFixture {

    // email, password (이메일은 고정된 값 사용한다고 가정)
    // 정적 팩토리 메서드 패턴(디자인 패턴의 생성 패턴 중 하나)
    public static Member.MemberBuilder defaultMember() {
        return Member.builder()
                .email("test1@gmail.com");
    }

    public static Member.MemberBuilder defaultMemberWithPassword() {
        return Member.builder()
                .password("1234");
    }
}
