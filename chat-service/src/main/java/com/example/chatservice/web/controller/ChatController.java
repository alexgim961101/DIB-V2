package com.example.chatservice.web.controller;

import com.example.chatservice.domain.chatroom.ChatRoom;
import com.example.chatservice.service.ChatService;
import com.example.chatservice.web.dto.ChatRoomDto;
import com.example.chatservice.web.dto.GetChatRoomDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/chat-service")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;

    // 채팅방 생성
    @PostMapping("/create/{userId}")
    public Mono<ResponseEntity<ChatRoom>> create(@PathVariable Long userId, @Valid @RequestBody ChatRoomDto chatRoomDto) {
        return chatService.createChatRoom(userId, chatRoomDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.internalServerError().build());
    }

    // 자신이 속한 채팅방 리스트 불러오기
    @GetMapping("/{userId}")
    public Flux<ResponseEntity<ChatRoom>> getAllByUserId(@PathVariable Long userId) {
        return chatService.getChatRoomList(userId)
                .map(ResponseEntity::ok);
    }

    // 채팅방 내용 불러오기
    @GetMapping("/detail/{chatRoomId}")
    public Mono<ResponseEntity<GetChatRoomDto>> getChatRoomDetail(@PathVariable String chatRoomId) {
        return chatService.getChatRoom(chatRoomId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.internalServerError().build());
    }

    // 채팅방 나가기
    @DeleteMapping("/{chatRoomId}/{userId}")
    public Mono<ResponseEntity<ChatRoom>> exitRoom(@PathVariable String chatRoomId, @PathVariable Long userId) {
        return chatService.exitChatRoom(chatRoomId, userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.internalServerError().build());
    }

    // 채팅방 입장하기
    @PostMapping("/enter/{userId}/{chatRoomId}")
    public Mono<ResponseEntity<ChatRoom>> enter(@PathVariable Long userId, @PathVariable String chatRoomId) {
        return chatService.enterChatRoom(userId, chatRoomId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.internalServerError().build());
    }
}
