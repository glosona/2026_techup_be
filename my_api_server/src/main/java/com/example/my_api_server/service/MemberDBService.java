package com.example.my_api_server.service;

import com.example.my_api_server.entity.Member;
import com.example.my_api_server.event.MemberSignUpEvent;
import com.example.my_api_server.repo.MemberDBRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor // 생성자 주입 DI
@Slf4j
public class MemberDBService {

    private final MemberDBRepo memberDBRepo;
    private final MemberPointService memberPointService;

    private final ApplicationEventPublisher publisher; // 이벤트를 보내줄 publisher

    @Transactional
    public Long signUp(String email, String password) throws IOException {
        Member member = Member.builder()
                .email(email)
                .password(password)
                .build();

        Member savedMember = memberDBRepo.save(member);

//        throw new IOException("외부 API 호출하다가 I/O 예외 터졌다고 가정"); // non-rollback
//        throw new RuntimeException("DB 예외 터졌다고 가정"); // rollback
//        sendNotification(); // Network I/O는 트랜잭션 분리를 해주는 것이 성능에 좋다.

//        memberPointService.changeAllUserData();

        publisher.publishEvent(new MemberSignUpEvent(savedMember.getId(), savedMember.getEmail()));

        return savedMember.getId();
    }

    // 이메일, 알림 가정
//    @Async // 비동기로(다른 스레드에게 일을) 시키겠다.
    public void sendNotification() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("알림 전송 완료");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void changeAllUserData() {
        List<Member> members = memberDBRepo.findAll();
    }

    public Member findMember(Long id) {
        return memberDBRepo.findById(id).orElseThrow();
    }


    // 테스트 매서드
    @Transactional(propagation = Propagation.REQUIRED, timeout = 2)
    public void tx1() {
        List<Member> members = memberDBRepo.findAll();
        members.stream()
                .forEach((m) -> {
                    log.info("member id = {}", m.getId());
                    log.info("member email = {}", m.getEmail());
                });

        memberPointService.changeAllUserData(); // AOP

        memberPointService.timeout(); // 타임아웃 테스트
    }
}
