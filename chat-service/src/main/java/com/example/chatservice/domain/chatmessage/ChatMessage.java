package com.example.chatservice.domain.chatmessage;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "chat_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    @Id
    private String chatMessageId;
    private String chatRoomId;
    private String content;
    private Long senderId;

    private Type type;

    private LocalDateTime createdAt;

    public static enum Type {
        START, MSG, END;
    }

    @Builder
    public ChatMessage(String chatMessageId, String chatRoomId, String content, Long senderId, Type type, LocalDateTime createdAt) {
        this.chatMessageId = chatMessageId;
        this.chatRoomId = chatRoomId;
        this.content = content;
        this.senderId = senderId;
        this.type = type;
        this.createdAt = createdAt;
    }

    //== 생성 메서드 ==//
    public static ChatMessage createChatMassage(String chatRoomId, String content, Long senderId) {
        return ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .content(content)
                .senderId(senderId)
                .type(Type.MSG)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static ChatMessage createChatMassage(String chatRoomId, Long senderId, Type type) {
        String content = "";
        if(type == Type.START) content = senderId + "님이 입장하셨습니다";
        else content = senderId + "님이 퇴장하셨습니다";

        return ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .content(content)
                .senderId(senderId)
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
