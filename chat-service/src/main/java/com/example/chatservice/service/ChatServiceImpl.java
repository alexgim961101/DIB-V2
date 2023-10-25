package com.example.chatservice.service;

import com.example.chatservice.domain.chatmessage.ChatMessage;
import com.example.chatservice.domain.chatmessage.ChatMessageRepo;
import com.example.chatservice.domain.chatroom.ChatRoom;
import com.example.chatservice.domain.chatroom.ChatRoomRepo;
import com.example.chatservice.util.SystemString;
import com.example.chatservice.web.dto.ChatMessageDto;
import com.example.chatservice.web.dto.ChatRoomDto;

import com.example.chatservice.web.dto.GetChatRoomDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService{
    private final ChatRoomRepo chatRoomRepo;
    private final ChatMessageRepo chatMessageRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SimpMessageSendingOperations template;
    private final ObjectMapper om;

    // TODO: 전반적으로 데이터를 찾지 못했을 때, 예외를 터트려 줄 것

    @Override
    public Mono<ChatRoom> createChatRoom(Long userId, ChatRoomDto chatRoomDto) {
        ChatRoom chatRoom = ChatRoom.createChatRoom(chatRoomDto.getArticleId(), chatRoomDto.getChatRoomName(), userId);
        return chatRoomRepo.save(chatRoom);

    }

    // TODO: 나중에 페이징 처리해 주면 더 좋음
    @Override
    public Flux<ChatRoom> getChatRoomList(Long userId) {
        return chatRoomRepo.findAllByParticipantIdsContains(userId);
    }

    @Override
    public Mono<GetChatRoomDto> getChatRoom(String chatRoomId) {
//        아래의 코드는 reactive 프로그래밍에 적합하지 못함
//        List<Long> users = new ArrayList<>();
//        Mono<ChatRoom> monoChatRoom = chatRoomRepo.findById(chatRoomId);
//        monoChatRoom.subscribe(chatRoom -> {
//            Set<Long> participantIds = chatRoom.getParticipantIds();
//
//            participantIds.forEach(id -> users.add(id));
//        }, error -> log.info("채팅방의 유저 조회 중 에러 발생!!!"));
//
//
//        Flux<ChatMessage> fluxChatMessage = chatMessageRepo.findAllByCharRoomId(chatRoomId);

        Mono<ChatRoom> monoChatRoom = chatRoomRepo.findById(chatRoomId);
        Flux<ChatMessage> fluxChatMessage = chatMessageRepo.findAllByChatRoomId(chatRoomId);

        return monoChatRoom.flatMap(cr -> {
            Mono<List<ChatMessage>> messageList = fluxChatMessage.collectList();

            return messageList.map(messages -> {
                return GetChatRoomDto.createChatRoomDto(chatRoomId, messages, cr.getChatRoomName(), cr.getParticipantIds());
            });
        });
    }



    // TODO: 체팅방의 인원 수가 0이면 지워주는 로직을 만들어야함
    @Override
    public Mono<ChatRoom> exitChatRoom(String chatRoomId, Long userId) {
        Mono<ChatRoom> monoChatRoom = chatRoomRepo.findById(chatRoomId);

        return monoChatRoom.flatMap(chatRoom -> {
            chatRoom.getParticipantIds().remove(userId);

            return chatRoomRepo.save(chatRoom);
        });
    }

    @Override
    public Mono<ChatRoom> enterChatRoom(Long userId, String chatRoomId) {
        Mono<ChatRoom> monoChatRoom = chatRoomRepo.findById(chatRoomId);

        return monoChatRoom.flatMap(chatRoom -> {
            chatRoom.getParticipantIds().add(userId);
            return chatRoomRepo.save(chatRoom);
        });
    }

    @Override
    public void sendMessage(String type, ChatMessageDto chatMessageDto) {
        log.info("===== kafka producer 실행 (메세지 보내기) : {} =====", chatMessageDto.getContent());
        ChatMessage chatMessage = null;

        if(type.equals("START")) {
            chatMessage = ChatMessage.createChatMassage(chatMessageDto.getChatRoomId(), chatMessageDto.getSenderId(), ChatMessage.Type.START);
            // chatMessage.setContent("al");
        }
        if(type.equals("END")) {
            chatMessage = ChatMessage.createChatMassage(chatMessageDto.getChatRoomId(), chatMessageDto.getSenderId(), ChatMessage.Type.END);
            // chatMessage.setContent("al");
        }
        if(type.equals("MSG")) {
            chatMessage = ChatMessage.createChatMassage(chatMessageDto.getChatRoomId(), chatMessageDto.getContent(), chatMessageDto.getSenderId());
        }

        log.info("===== kafka 보내기 전 데이터 확인 : {}", chatMessage);

        chatMessageRepo.save(chatMessage)
                .subscribe(savedChatMessage -> {
                    try {
                        // 저장된 메시지를 JSON으로 직렬화합니다.
                        String json = om.writeValueAsString(savedChatMessage);
                        String topic = SystemString.KAFKA_CHATTING_TOPIC;

                        // JSON을 Kafka로 전송합니다.
                        kafkaTemplate.send(topic, json);
                        log.info("===== [kafka] 메시지 전송 완료 : {} =====", savedChatMessage);
                    } catch (JsonProcessingException e) {
                        log.error("===== [에러] 메세지를 Json으로 변환하는 과정에서 오류 발생 =====", e);
                    }
                }, error -> {
                    log.error("메시지 저장 중 오류 발생", error);
                });
    }

    @Override
    @KafkaListener(topics = SystemString.KAFKA_CHATTING_TOPIC)
    public void receiveMessage(String message) {
        log.info("===== kafca consumer 실행 (메세지 받기) : {} =====", message);
        Map<Object, Object> map = new HashMap<>();
        try {
            map = om.readValue(message, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.info("===== [에러] Json을 객체로 파싱하는 과정에서 오류 발생 =====");
        }

        log.info("===== [데이터 확인] : {} =====", map.get("chatRoomId"));
        template.convertAndSend("/sub/" + map.get("chatRoomId"), map);
    }
}
