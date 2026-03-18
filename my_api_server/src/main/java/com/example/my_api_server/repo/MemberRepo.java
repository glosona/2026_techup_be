package com.example.my_api_server.repo;

import com.example.my_api_server.entity.Member;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// DAO: DB와 통신하는 객체

@Component // bean으로 등록
public class MemberRepo {

    Map<Long, Member> members = new HashMap<>(); // JVM Heap 메모리에 올라갑니다.

    // 연산(저장, 수정, 삭제, 조회)

    // 저장
    public Long saveMember(String email, String password) {
        Random random = new Random();
        long id = random.nextLong();
        Member member = Member.builder()
                .id(id)
                .email(email)
                .password(password)
                .build();

        members.put(id, member);

        return id;
    }

    // 조회
    public Member findMember(Long id) {
        return members.get(id);
    }
}
