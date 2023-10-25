package com.example.chatservice.service;

import com.example.chatservice.domain.chatmessage.ChatMessageRepo;
import com.example.chatservice.domain.chatroom.ChatRoom;
import com.example.chatservice.domain.chatroom.ChatRoomRepo;
import com.example.chatservice.web.dto.ChatRoomDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {
    @Mock
    private ChatRoomRepo chatRoomRepo;
    @Mock
    private ChatMessageRepo chatMessageRepo;
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private SimpMessageSendingOperations messageSendingOperations;
    @InjectMocks
    private ChatServiceImpl chatService;

    // 테스터 데이터
    private final Long userId = 123L;
    private final Long articleId = 11L;
    private final String chatRoomName = "chatRoom";
    private final String roomId = "roomA";

    @Test
    @DisplayName(value = "채팅방 생성")
    void createChatRoomTest() {
        // given
        ChatRoomDto chatRoomDto = ChatRoomDto.createChatRoomDto(articleId, chatRoomName);
        ChatRoom mockChatRoom = ChatRoom.createChatRoom(articleId, chatRoomName, userId);
        mockChatRoom.setRoomId(roomId);

        when(chatRoomRepo.save(any(ChatRoom.class))).thenReturn(Mono.just(mockChatRoom));

        // when
        Mono<ChatRoom> chatRoom = chatService.createChatRoom(userId, chatRoomDto);

        // then
        StepVerifier.create(chatRoom)
                .expectNextMatches(created ->
                    created.getChatRoomName().equals(mockChatRoom.getChatRoomName()) &&
                    created.getArticleId().equals(mockChatRoom.getArticleId())
                )
                .verifyComplete();
    }
}
