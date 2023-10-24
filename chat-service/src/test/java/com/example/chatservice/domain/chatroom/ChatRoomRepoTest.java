package com.example.chatservice.domain.chatroom;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class ChatRoomRepoTest {
    @Autowired
    private ChatRoomRepo chatRoomRepo;

    @Test
    @DisplayName(value = "채팅방 생성 테스트")
    void createChatRoom(){
        // given
        ChatRoom chatRoom = ChatRoom.createChatRoom(1L, 10L);

        // when
        Mono<ChatRoom> monoChatRoom = chatRoomRepo.save(chatRoom);

        // then
        StepVerifier.create(monoChatRoom)
                .assertNext(createdChatRoom -> {
                    assertEquals(createdChatRoom.getArticleId(), chatRoom.getArticleId());
                    assertEquals(createdChatRoom.getParticipantIds().contains(10L), chatRoom.getParticipantIds().contains(10L));
                })
                .verifyComplete();


    }

}