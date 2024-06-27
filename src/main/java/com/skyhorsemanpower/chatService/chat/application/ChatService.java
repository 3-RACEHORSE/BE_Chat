package com.skyhorsemanpower.chatService.chat.application;

import com.skyhorsemanpower.chatService.chat.data.dto.BeforeChatRoomDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomMemberResponseDto;
import com.skyhorsemanpower.chatService.chat.data.dto.UnReadChatCountResponseDto;
import com.skyhorsemanpower.chatService.chat.data.dto.UpdateProfileImageRequestDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomTitleResponseDto;
import com.skyhorsemanpower.chatService.chat.data.dto.LeaveChatRoomDto;
import com.skyhorsemanpower.chatService.chat.data.dto.SendChatRequestDto;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatRoomResponseVo;
import com.skyhorsemanpower.chatService.chat.data.vo.GetChatVo;
import com.skyhorsemanpower.chatService.chat.data.vo.LastChatVo;
import com.skyhorsemanpower.chatService.chat.data.vo.PreviousChatResponseVo;
import java.time.LocalDateTime;
import java.util.List;
import reactor.core.publisher.Flux;

public interface ChatService {

    void createChatRoom(BeforeChatRoomDto beforeChatRoomDto);

    void sendChat(SendChatRequestDto sendChatRequestDto, String uuid);

    Flux<GetChatVo> getChat(String roomNumber, String uuid);

    List<ChatRoomResponseVo> getChatRoomsByUuid(String uuid);

    PreviousChatResponseVo getPreviousChat(String roomNumber, LocalDateTime enterTime, int page,
        int size);

    void leaveChatRoom(LeaveChatRoomDto leaveChatRoomDto);

    LastChatVo getLastChatSync(String uuid, String roomNumber);

    Flux<LastChatVo> getLastChat(String uuid, String roomNumber);

    ChatRoomTitleResponseDto getChatRoomTitle(String uuid, String roomNumber);

    void updateProfileImage(UpdateProfileImageRequestDto updateProfileImageRequestDto);

    List<ChatRoomMemberResponseDto> getChatRoomMembers(String roomNumber);

    UnReadChatCountResponseDto getUnreadChatCount(String roomNumber, String uuid);
}
