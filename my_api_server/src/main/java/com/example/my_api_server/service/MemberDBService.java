package com.example.my_api_server.service;

import com.example.my_api_server.entity.Member;
import com.example.my_api_server.repo.MemberDBRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // 생성자 주입 DI
@Slf4j
public class MemberDBService {

    private final MemberDBRepo memberDBRepo;

    @Transactional
    public Long signUp(String email, String password) {
        Member member = Member.builder()
                .email(email)
                .password(password)
                .build();

        Member savedMember = memberDBRepo.save(member);

        sendNotification();

        return savedMember.getId();
    }


    public void sendNotification() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("알림 전송 완료");
    }
}
