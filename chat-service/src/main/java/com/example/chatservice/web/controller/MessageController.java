package com.example.chatservice.web.controller;

import com.example.chatservice.service.ChatService;
import com.example.chatservice.web.dto.ChatMessageDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

// 웹 소켓을 사용한 통신에서는 RestController 대신 Controller를 사용 -> WebSocket 세션과 관련된 작업들이 HTTP 요청의 생명주기와는 다르기 때문
@Controller
@RequiredArgsConstructor
public class MessageController {
    private final ChatService chatService;

    // 웹 소켓 통신에서는 유효성 검사를 수동으로 해줘야 함
    @MessageMapping("/message/{type}")
    public void send(@DestinationVariable String type, ChatMessageDto chatMessageDto) {
        chatService.sendMessage(type, chatMessageDto);
    }
}
