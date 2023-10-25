package com.example.chatservice.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatMessageDto {

    private String chatRoomId;
    private String content;
    private Long senderId;

    @Builder
    private ChatMessageDto(String chatRoomId, String content, Long senderId) {
        this.chatRoomId = chatRoomId;
        this.content = content;
        this.senderId = senderId;
    }
}
