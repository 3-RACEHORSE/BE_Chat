package com.skyhorsemanpower.chatService.chat.application;

import com.skyhorsemanpower.chatService.chat.data.dto.BeforeChatRoomDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ExtractAuctionInformationWithMemberUuidsDto;
import com.skyhorsemanpower.chatService.chat.data.dto.LeaveChatRoomDto;
import com.skyhorsemanpower.chatService.chat.data.dto.SendChatRequestDto;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatRoomResponseVo;
import com.skyhorsemanpower.chatService.chat.data.vo.GetChatVo;
import com.skyhorsemanpower.chatService.chat.data.vo.LastChatVo;
import com.skyhorsemanpower.chatService.chat.data.vo.PreviousChatResponseVo;
import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import java.time.LocalDateTime;
import java.util.List;
import reactor.core.publisher.Flux;

public interface ChatService {
    void createChatRoom(
        ExtractAuctionInformationWithMemberUuidsDto extractAuctionInformationWithMemberUuidsDto);
    void sendChat(SendChatRequestDto sendChatRequestDto, String uuid);
    Flux<GetChatVo> getChat(String roomNumber, String uuid);
    List<ChatRoomResponseVo> getChatRoomsByUuid(String uuid);
    PreviousChatResponseVo getPreviousChat(String roomNumber, LocalDateTime enterTime, int page, int size);
//    void enteringMember(String uuid, String roomNumber);
//    String findOtherMemberUuid(String uuid, String roomNumber);

//    int getUnreadChatCount(String roomNumber, String uuid);
    void leaveChatRoom(LeaveChatRoomDto leaveChatRoomDto);
    LastChatVo getLastChatSync(String uuid, String roomNumber);

    Flux<LastChatVo> getLastChat(String uuid, String roomNumber);

    void convertToChatRoomData(BeforeChatRoomDto beforeChatRoomDto);
}
