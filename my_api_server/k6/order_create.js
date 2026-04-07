import http from 'k6/http';
import {check, sleep} from 'k6';

export const options = {
    // 테스트 설정 (예: 10명이 30초 동안 반복 네트워크(HTTP, HTTPS, WS, WSS) 호출)
    vus: 3000, // 가상 유저수
    duration: '30s', // 테스트할 총 시간
    //SLA(Service Level Agreement) 준수: 서비스가 보장해야 하는 최소한의 속도를 강제할 수 있음
    thresholds: {
        // 1. 응답 시간 기준: 95%의 요청이 500ms(0.5초) 이내여야 함
        http_req_duration: ['p(95)<2000'],

        // 2. 에러율 기준: 에러 발생률이 1% 미만이어야 함
        http_req_failed: ['rate<0.01'],

        // 3. 특정 요청에 대한 기준 (주문 API 전용)
        'http_req_duration{url:http://localhost:8080/api/v1/orders}': ['p(99)<3000'],
    },
};

export default function () {
    const url = 'http://localhost:8080/api/v1/orders'; // 서버 주소에 맞게 수정하세요

    // DTO 구조에 맞춘 페이로드 설정
    const payload = JSON.stringify({
        memberId: 1,
        productId: [1, 2], // 리스트 형태
        count: [1, 1],           // 리스트 형태
        orderTime: new Date().toISOString(), // LocalDateTime 형식에 대응
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // POST 요청 전송
    const res = http.post(url, payload, params);

    // 결과 검증 (HTTP 상태 코드가 200 또는 201인지 확인)
    check(res, {
        'is status 200': (r) => r.status === 200,
    });

    // 다음 요청 전 휴식 (1초)
    sleep(1);
}
