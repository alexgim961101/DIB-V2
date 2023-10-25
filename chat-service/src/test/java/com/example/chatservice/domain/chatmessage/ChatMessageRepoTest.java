package com.example.chatservice.domain.chatmessage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class ChatMessageRepoTest {
    @Autowired
    private ChatMessageRepo chatMessageRepo;

    @Test
    @DisplayName(value = "저장 테스트")
    void saveChatMessage() {
        // given
        ChatMessage chatMassage = ChatMessage.createChatMassage("1", "hello", 1L);

        // when
        Mono<ChatMessage> monoChatMessage = chatMessageRepo.save(chatMassage);

        // then
        StepVerifier.create(monoChatMessage)
                .assertNext(createdChatMesssage -> {
                    assertEquals(chatMassage.getChatMessageId(), createdChatMesssage.getChatMessageId());
                })
                .verifyComplete();
    }

}