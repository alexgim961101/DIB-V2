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

    @Override
    public Mono<ChatRoomDto> createChatRoom(Long userId, ChatRoomDto chatRoomDto) {
        ChatRoom chatRoom = ChatRoom.createChatRoom(chatRoomDto.getArticleId(), chatRoomDto.getChatRoomName(), userId);
        Mono<ChatRoom> monoChatRoom = chatRoomRepo.save(chatRoom);

        return monoChatRoom.map(savedChatRoom -> ChatRoomDto.fromEntity(savedChatRoom));
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
        Flux<ChatMessage> fluxChatMessage = chatMessageRepo.findAllByCharRoomId(chatRoomId);

        return monoChatRoom.flatMap(cr -> {
            Mono<List<ChatMessage>> messageList = fluxChatMessage.collectList();

            return messageList.map(messages -> {
                return GetChatRoomDto.createChatRoomDto(chatRoomId, messages, cr.getChatRoomName(), cr.getParticipantIds());
            });
        });
    }

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
        log.info("===== kafka producer 실행 (메세지 보내기) ====");
        ObjectMapper om = new ObjectMapper();
        String json = "";
        ChatMessage chatMessage = null;

        if(type.equals("START")) {
            chatMessage = ChatMessage.createChatMassage(chatMessageDto.getCharRoomId(), chatMessageDto.getSenderId(), ChatMessage.Type.START);
        }
        if(type.equals("END")) {
            chatMessage = ChatMessage.createChatMassage(chatMessageDto.getCharRoomId(), chatMessageDto.getSenderId(), ChatMessage.Type.END);
        }
        if(type.equals("MSG")) {
            chatMessage = ChatMessage.createChatMassage(chatMessageDto.getCharRoomId(), chatMessageDto.getContent(), chatMessageDto.getSenderId());
        }
        try {
            json = om.writeValueAsString(chatMessage);
        } catch (JsonProcessingException e) {
            log.info("===== [에러] 메세지를 Json으로 변환하는 과정에서 오류 발생 =====");
        }

        chatMessageRepo.save(chatMessage);

        String topic = SystemString.KAFKA_CHATTING_TOPIC;
        kafkaTemplate.send(topic, json);
        log.info("===== [kafka] 메세지 전송 완료 : '"+ chatMessage.getContent() +"' =====");
    }

    @Override
    @KafkaListener(topics = SystemString.KAFKA_CHATTING_TOPIC)
    public void receiveMessage(String message) {
        log.info("===== kafca consumer 실행 (메세지 받기) =====");
        Map<Object, Object> map = new HashMap<>();
        ObjectMapper om = new ObjectMapper();
        try {
            map = om.readValue(message, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException e) {
            log.info("===== [에러] Json을 객체로 파싱하는 과정에서 오류 발생 =====");
        }

        log.info("===== [데이터 확인] : {} =====", map);
        template.convertAndSend("/sub/" + map.get("chatRoomId"), map);
    }
}
