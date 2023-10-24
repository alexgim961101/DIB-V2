package com.example.chatservice.domain.chatroom;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ChatRoomRepo extends ReactiveMongoRepository<ChatRoom, String> {
    Flux<ChatRoom> findAllByParticipantIdsContains(String participantId);
}
