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
    private String charRoomId;
    private String content;
    private String sender;

    private Type type;

    private LocalDateTime createdAt;

    public static enum Type {
        START, MSG, END;
    }

    @Builder
    public ChatMessage(String chatMessageId, String charRoomId, String content, String sender, Type type, LocalDateTime createdAt) {
        this.chatMessageId = chatMessageId;
        this.charRoomId = charRoomId;
        this.content = content;
        this.sender = sender;
        this.type = type;
        this.createdAt = createdAt;
    }

    //== 생성 메서드 ==//
    public static ChatMessage createChatMassage(String charRoomId, String content, String sender) {
        return ChatMessage.builder()
                .charRoomId(charRoomId)
                .content(content)
                .sender(sender)
                .type(Type.MSG)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static ChatMessage createChatMassage(String charRoomId, String sender, Type type) {
        String content = "";
        if(type == Type.START) content = sender + "님이 입장하셨습니다";
        else content = sender + "님이 퇴장하셨습니다";

        return ChatMessage.builder()
                .charRoomId(charRoomId)
                .content(content)
                .sender(sender)
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
