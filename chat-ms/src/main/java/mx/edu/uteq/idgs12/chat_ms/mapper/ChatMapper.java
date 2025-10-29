package mx.edu.uteq.idgs12.chat_ms.mapper;

import mx.edu.uteq.idgs12.chat_ms.client.UserClient;
import mx.edu.uteq.idgs12.chat_ms.dto.*;
import mx.edu.uteq.idgs12.chat_ms.entity.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Comparator;

@Component
public class ChatMapper {

    private final UserClient userClient;

    public ChatMapper(UserClient userClient) {
        this.userClient = userClient;
    }

    // === Conversation Mapping ===
    public ConversationDTO toConversationDTO(Conversation conv, Long currentUserId) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conv.getId());
        dto.setType(conv.getType());
        dto.setTitle(conv.getTitle());
        dto.setCreatedAt(conv.getCreatedAt());

        // Enriquecer con datos del otro usuario si es privado
        if ("PRIVATE".equalsIgnoreCase(conv.getType()) && currentUserId != null) {
            conv.getParticipants().stream()
                    .filter(p -> !p.getUserId().equals(currentUserId))
                    .findFirst()
                    .ifPresent(other -> enrichUserInfo(dto, other.getUserId()));
        }

        conv.getMessages().stream()
                .max(Comparator.comparing(Message::getSentAt))
                .ifPresent(last -> {
                    dto.setLastMessage(last.getContent());
                    dto.setLastMessageTime(last.getSentAt());
                    dto.setLastMessageMine(last.getSenderId().equals(currentUserId));
                    dto.setLastMessageSender(getUserFullName(last.getSenderId()));
                });

        return dto;
    }

    // === Message Mapping ===
    public MessageDTO toMessageDTO(Message msg) {
        MessageDTO dto = new MessageDTO();
        dto.setId(msg.getId());
        dto.setConversationId(msg.getConversation().getId());
        dto.setSenderId(msg.getSenderId());
        dto.setContent(msg.getContent());
        dto.setRead(msg.isRead());
        dto.setSentAt(msg.getSentAt());
        enrichSenderInfo(dto);
        return dto;
    }

    // === Participant Mapping ===
    public ParticipantDTO toParticipantDTO(Participant p) {
        ParticipantDTO dto = new ParticipantDTO();
        dto.setId(p.getId());
        dto.setConversationId(p.getConversation().getId());
        dto.setUserId(p.getUserId());
        dto.setRole(p.getRole());
        dto.setJoinedAt(p.getJoinedAt());
        return dto;
    }

    // === Helpers ===
    private void enrichUserInfo(ConversationDTO dto, Long userId) {
        try {
            Map<String, Object> user = userClient.getUserById(userId);
            if (user != null) {
                dto.setTitle(getFullName(user));
                dto.setAvatar((String) user.get("profileImage"));
            }
        } catch (Exception e) {
            dto.setTitle("Chat privado");
        }
    }

    private void enrichSenderInfo(MessageDTO dto) {
        try {
            Map<String, Object> sender = userClient.getUserById(dto.getSenderId());
            if (sender != null) {
                dto.setSenderName(getFullName(sender));
                dto.setSenderAvatar((String) sender.get("profileImage"));
            }
        } catch (Exception ignored) {}
    }

    private String getUserFullName(Long userId) {
        try {
            Map<String, Object> user = userClient.getUserById(userId);
            return user != null ? getFullName(user) : "Usuario";
        } catch (Exception e) {
            return "Usuario";
        }
    }

    private String getFullName(Map<String, Object> user) {
        return user.get("firstName") + " " + user.get("lastName");
    }
}
