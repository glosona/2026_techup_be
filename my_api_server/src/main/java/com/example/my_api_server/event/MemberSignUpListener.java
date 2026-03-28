package com.example.my_api_server.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class MemberSignUpListener { // 이벤트를 받는 리스너

    // 아직 새로운 일꾼은 안 붙여서 일꾼1번이 이 일을 진행
    // 스레드 1번이 DB 안정성(커밋) 확인하고 나서 내 로직을 수행

    @Async // 다른 일꾼에게 맡겨야한다.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotification(MemberSignUpEvent event) {
        log.info("member ID = {}", event.getId());
        log.info("member Email = {}", event.getEmail());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("알림 전송 완료");
    }

}
