package com.example.chatservice.domain.chatmessage;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ChatMessageRepo extends ReactiveMongoRepository<ChatMessage, String> {
    // Flux : 데이터를 계속 받겠다는 의미 (Response를 유지하면서 계속 데이터를 흘려보냄)
    // @Tailable  // 커서를 안닫고 계속 유지 -> 한번 해당 명령어가 실행되면 계속 데이터를 읽음
//    @Query("{sender: ?0, receiver: ?1}")
//    Flux<ChatMessage> mFindBySender(String sender, String receiver);
    Flux<ChatMessage> findAllByCharRoomId(String chatRoomId);
}
