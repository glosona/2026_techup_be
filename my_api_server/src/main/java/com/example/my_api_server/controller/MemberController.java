package com.example.my_api_server.controller;

import ch.qos.logback.core.util.StringUtil;
import com.example.my_api_server.service.MemberDBService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberDBService memberService;

    // 회원가입
    @PostMapping
    public Long signUp(@Validated @RequestBody MemberSignUpDto dto) {
        if (StringUtil.isNullOrEmpty(dto.email()) || StringUtil.isNullOrEmpty(dto.password())) {
            throw new RuntimeException("email or password is null");
        }

        Long memberId = memberService.signUp(dto.email(), dto.password());

        return memberId;
    }

    // 회원조회
//    @GetMapping("/{id}")
//    public Member findMember(@PathVariable Long id) {
//        Member member = memberService.findMember(id);
//        return member;
//    }
}
