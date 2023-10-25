package com.example.chatservice.web.dto;

import com.example.chatservice.domain.chatmessage.ChatMessage;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class GetChatRoomDto {
    private String chatRoomId;
    private List<ChatMessage> chatMessageList;
    private String chatRoomName;
    // TODO: user-service와 연결시 Long -> UserDto로 변경할 것
    private Set<Long> users;

    @Builder
    public GetChatRoomDto(String chatRoomId, List<ChatMessage> chatMessageList, String chatRoomName, Set<Long> users) {
        this.chatRoomId = chatRoomId;
        this.chatMessageList = chatMessageList;
        this.chatRoomName = chatRoomName;
        this.users = users;
    }

    //== 생성 메서드 ==//
    public static GetChatRoomDto createChatRoomDto(String chatRoomId, List<ChatMessage> chatMessageList, String chatRoomName, Set<Long> users) {
        return GetChatRoomDto.builder()
                .chatRoomId(chatRoomId)
                .chatMessageList(chatMessageList)
                .chatRoomName(chatRoomName)
                .users(users)
                .build();
    }
}
