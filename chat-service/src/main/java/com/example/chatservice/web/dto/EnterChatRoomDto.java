package com.example.chatservice.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class EnterChatRoomDto {
    private String chatRoomName;
    // TODO: user-service와 연결시 Long -> UserDto로 변경할 것
    private List<String> users;
}
