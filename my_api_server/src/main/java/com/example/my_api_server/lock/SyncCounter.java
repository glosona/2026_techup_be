package com.example.my_api_server.lock;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
public class SyncCounter {

    private int count = 0; // 공유영역값(Heap)

    static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        int threadCount = 3;
        SyncCounter counter = new SyncCounter();

        // 스레드 생성
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(counter::increaseCount);
            thread.start();
            threads.add(thread);
        }

        // 스레드가 일이 다 끝날때까지 기다림
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        log.info("기대값 : {}", threadCount);
        log.info("실제값 : {}", counter.getCount());
    }

    // 메서드 단위의 sync 메서드 실행자체에 대해서 락을 얻어 순서를 제어
    private synchronized void increaseCount() {
        // 스레드 1번이 들어오면서 락 획득
        Thread.State state = Thread.currentThread().getState();
        log.info("state1 = {}", state.toString());

        // 해당 범위만 락을 얻겠다!
//        synchronized (this) { // 락으로 순서 제어
//            log.info("state2 = {}", state.toString());
//            count++; // 연산
//        }

        // 스레드 1번이 락 반환
        log.info("state3 = {}", state.toString());
    }

}



