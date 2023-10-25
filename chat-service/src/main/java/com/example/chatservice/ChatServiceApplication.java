package com.example.chatservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 에러 1. com.fasterxml.jackson.databind.exc.MismatchedInputException: No content to map due to end-of-input
 *  at [Source: (String)""; line: 1, column: 0]
 *
 * reactive 프로그래밍에서 데이터가 방행되지도 않았는데 먼저 데이터를 보내려고 해서 생긴 문제
 * 1). subscribe 함수를 사용해서 해결 ->  저장 동작이 성공적으로 반환 될 때 동장
 *
 * 에러 2. com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.LocalDateTime` not supported by default: add Module
 *
 * Jackson 라이브러리에서 LocalDateTime을 지원하지 않기 때문에 생긴 오류
 * 1). implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2' 추가
 * 2). ObjectMapper 커스텀 configuration
 * 3). 빈으로 등록하여 사용
 * */

@SpringBootApplication
//@EnableDiscoveryClient
public class ChatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServiceApplication.class, args);
    }

}
