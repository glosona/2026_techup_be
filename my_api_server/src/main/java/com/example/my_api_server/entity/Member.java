package com.example.my_api_server.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Member {

    private Long id;

    private String email;
    private String password;
}
