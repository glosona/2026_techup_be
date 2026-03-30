package com.example.my_api_server.lock;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Slf4j
public class ReentrantCounter {

    private final ReentrantLock lock = new ReentrantLock();

    private int count = 0; // 공유영역값(Heap)

    static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        int threadCount = 1000;
        ReentrantCounter counter = new ReentrantCounter();

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

    private void increaseCount() {
        this.lock.lock(); // 첫번째 락

        try {
            if (this.lock.tryLock(3, TimeUnit.SECONDS)) { // 두번째 락
                try {
                    log.info("락 획득 후 연산 시작");
                    this.count++;
                    Thread.sleep(4000);
                } finally {
                    this.lock.unlock(); // 첫번째 락 반납
                }
            } else {
                // 3초 안에 락 획득 못함
                log.info("3초 안에 락 획득 못함");
            }
        } catch (InterruptedException e) {
            log.info("작업 중단");
            throw new RuntimeException(e);
        }

//        try {
//            this.count++;
//        } finally {
//            this.lock.unlock(); // 락 반환(개발자가 원하는 시점에 락 획득/반납 제어 가능)
//        }
    }

}



