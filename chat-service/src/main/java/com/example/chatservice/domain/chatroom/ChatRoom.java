package com.example.chatservice.domain.chatroom;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Document(collection = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {
    @Id
    private String roomId;
    // 연결된 게시글 아이디
    private Long articleId;
    // 채팅방 이름
    private String chatRoomName;
    // 채팅방에 입장한 사람들
    private Set<Long> participantIds;
    // 채팅방 생성일
    private LocalDateTime createdAt;
    // 마지막 채팅 일자
    private LocalDateTime updatedAt;

    @Builder
    public ChatRoom(String roomId, Long articleId, String chatRoomName, Set<Long> participantIds, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.roomId = roomId;
        this.articleId = articleId;
        this.chatRoomName = chatRoomName;
        this.participantIds = participantIds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    //== 생성 메서드 ==//
    public static ChatRoom createChatRoom(Long articleId, String chatRoomName, Long participantId) {
        HashSet<Long> set = new HashSet<>();
        set.add(participantId);
        return ChatRoom.builder()
                .articleId(articleId)
                .chatRoomName(chatRoomName)
                .participantIds(set)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

    }

}
