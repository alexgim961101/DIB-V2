package com.example.chatservice.web.dto;

import com.example.chatservice.domain.chatroom.ChatRoom;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatRoomDto {
    private String roomId;
    private Long articleId;
    private String chatRoomName;
    private Integer peopleNum;
    private LocalDateTime updatedAt;

    @Builder
    public ChatRoomDto(String roomId, Long articleId, String chatRoomName, Integer peopleNum, LocalDateTime updatedAt) {
        this.roomId = roomId;
        this.articleId = articleId;
        this.chatRoomName = chatRoomName;
        this.peopleNum = peopleNum;
        this.updatedAt = updatedAt;
    }

    //== 생성 메서드 ==//
    public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .roomId(chatRoom.getRoomId())
                .articleId(chatRoom.getArticleId())
                .chatRoomName(chatRoom.getChatRoomName())
                .peopleNum(chatRoom.getParticipantIds().size())
                .updatedAt(chatRoom.getUpdatedAt())
                .build();
    }

}
