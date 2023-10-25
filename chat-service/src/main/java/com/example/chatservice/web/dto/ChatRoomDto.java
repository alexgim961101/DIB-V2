package com.example.chatservice.web.dto;

import com.example.chatservice.domain.chatroom.ChatRoom;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatRoomDto {
    private Long articleId;
    private String chatRoomName;

    @Builder
    private ChatRoomDto(Long articleId, String chatRoomName) {
        this.articleId = articleId;
        this.chatRoomName = chatRoomName;
    }

    //== 생성 메서드 ==//
    public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .articleId(chatRoom.getArticleId())
                .chatRoomName(chatRoom.getChatRoomName())
                .build();
    }

    public static ChatRoomDto createChatRoomDto(Long articleId, String chatRoomName) {
        return ChatRoomDto.builder()
                .articleId(articleId)
                .chatRoomName(chatRoomName)
                .build();
    }

}
