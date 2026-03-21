package com.example.my_api_server.controller;

import ch.qos.logback.core.util.StringUtil;
import com.example.my_api_server.entity.Member;
import com.example.my_api_server.service.MemberDBService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberDBService memberService;

    // 회원 가입
    @PostMapping
    public Long signUp(@Validated @RequestBody MemberSignUpDto dto) {
        if (StringUtil.isNullOrEmpty(dto.email()) || StringUtil.isNullOrEmpty(dto.password())) {
            throw new RuntimeException("email or password is null");
        }

        Long memberId = null;
        try {
            memberId = memberService.signUp(dto.email(), dto.password());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return memberId;
    }

    // 회원 조회
    @GetMapping("/{id}")
    public Member findMember(@PathVariable Long id) {
        Member member = memberService.findMember(id);
        return member;
    }
}
