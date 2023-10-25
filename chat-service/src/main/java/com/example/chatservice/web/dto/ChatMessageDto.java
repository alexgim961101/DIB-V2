package com.example.chatservice.web.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatMessageDto {
    private String charRoomId;
    private String content;
    private Long senderId;
    private LocalDateTime createdAt;

    @Builder
    private ChatMessageDto(String charRoomId, String content, Long senderId, LocalDateTime createdAt) {
        this.charRoomId = charRoomId;
        this.content = content;
        this.senderId = senderId;
        this.createdAt = createdAt;
    }
}
