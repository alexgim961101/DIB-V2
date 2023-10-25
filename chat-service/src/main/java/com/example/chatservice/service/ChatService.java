package com.example.chatservice.service;

import com.example.chatservice.domain.chatroom.ChatRoom;
import com.example.chatservice.web.dto.ChatMessageDto;
import com.example.chatservice.web.dto.ChatRoomDto;
import com.example.chatservice.web.dto.GetChatRoomDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatService {

    /**
     * 채팅방 생성
     *
     * 채팅방 생성시 본인은 무조건 채팅방에 속함
     * */
    Mono<ChatRoom> createChatRoom(Long userId, ChatRoomDto chatRoomDto);

    /**
     * 자신이 속한 채팅방 리스트 불러오기
     * */
    Flux<ChatRoom> getChatRoomList(Long userId);


    /**
     * 기존 채팅방 불러오기
     *
     * 기존의 메세지를 50개씩 불러오기 (한번에 다 불러오면 성능 저하 발생할 것)
     * 채팅방 이름
     * 채팅방에 속한 사람들의 정보 불러오기
     * */
     Mono<GetChatRoomDto> getChatRoom(String chatRoomId);


    /**
     * 채팅방 나가기
     *
     * 채팅방에 있는 유저의 정보 삭제
     * ㄷㅐ화 내용은 남아있도록 유지
     * */
    Mono<ChatRoom> exitChatRoom(String chatRoomId, Long userId);


    /**
     * 신규 채팅방 입장
     *
     * 채팅방의 이름, 속한 사람들의 정보
     * 채팅방 인원 수 증가
     * */
    Mono<ChatRoom> enterChatRoom(Long userId, String chatRoomId);


    /**
     * 채팅 메세지 보내기
     *
     * 메세지 보내기 (입장, 퇴장, 일반 메세지)
     * 채팅방의 마지막 메세지 및 업데이트 일 변경
     * */
    void sendMessage(String type, ChatMessageDto chatMessageDto);

    /**
     * 채팅 메세지 받기
     *
     * */
    void receiveMessage(String message);
}
