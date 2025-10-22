package mx.edu.uteq.idgs12.chat_ms.controller;

import mx.edu.uteq.idgs12.chat_ms.client.UserClient;
import mx.edu.uteq.idgs12.chat_ms.dto.ConversationDTO;
import mx.edu.uteq.idgs12.chat_ms.dto.MessageDTO;
import mx.edu.uteq.idgs12.chat_ms.dto.ParticipantDTO;
import mx.edu.uteq.idgs12.chat_ms.entity.Conversation;
import mx.edu.uteq.idgs12.chat_ms.entity.Message;
import mx.edu.uteq.idgs12.chat_ms.entity.Participant;
import mx.edu.uteq.idgs12.chat_ms.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;
    private final UserClient userClient;

    public ChatController(ChatService chatService, UserClient userClient) {
        this.chatService = chatService;
        this.userClient = userClient;
    }

    // === Conversations ===
    @GetMapping("/{userId}")
    public List<ConversationDTO> listConversationsByUser(@PathVariable Long userId) {
        return chatService.getConversationsByUser(userId).stream().map(conv -> {
            ConversationDTO dto = new ConversationDTO();
            dto.setId(conv.getId());
            dto.setTitle(conv.getTitle());
            dto.setType(conv.getType());
            dto.setCreatedAt(conv.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ConversationDTO> getConversation(@PathVariable Long id) {
        return chatService.getConversationById(id)
                .map(conv -> {
                    ConversationDTO dto = new ConversationDTO();
                    dto.setId(conv.getId());
                    dto.setTitle(conv.getTitle());
                    dto.setType(conv.getType());
                    dto.setCreatedAt(conv.getCreatedAt());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ConversationDTO createConversation(@RequestBody Conversation conversation) {
        Conversation saved = chatService.saveConversation(conversation);
        ConversationDTO dto = new ConversationDTO();
        dto.setId(saved.getId());
        dto.setTitle(saved.getTitle());
        dto.setType(saved.getType());
        dto.setCreatedAt(saved.getCreatedAt());
        return dto;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long id) {
        chatService.deleteConversation(id);
        return ResponseEntity.noContent().build();
    }

    // === Messages ===
    @GetMapping("/messages/{conversationId}")
    public List<MessageDTO> getMessages(@PathVariable Long conversationId) {
        return chatService.getMessagesByConversation(conversationId).stream().map(msg -> {
            MessageDTO dto = new MessageDTO();
            dto.setId(msg.getId());
            dto.setConversationId(msg.getConversation().getId());
            dto.setSenderId(msg.getSenderId());
            dto.setContent(msg.getContent());
            dto.setRead(msg.isRead());
            dto.setSentAt(msg.getSentAt());

            // Obtener información del usuario
            try {
                Map<String, Object> userData = userClient.getUserById(msg.getSenderId());
                if (userData != null) {
                    String firstName = (String) userData.get("firstName");
                    String lastName = (String) userData.get("lastName");
                    dto.setSenderName(firstName + " " + lastName);
                    dto.setSenderAvatar((String) userData.get("profileImage"));
                }
            } catch (Exception e) {
                System.err.println("Error obteniendo datos del usuario " + msg.getSenderId() + ": " + e.getMessage());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @PostMapping("/messages/{conversationId}")
    public MessageDTO sendMessage(@PathVariable Long conversationId, @RequestBody Message message) {
        Conversation conversation = new Conversation();
        conversation.setId(conversationId);
        message.setConversation(conversation);

        Message saved = chatService.saveMessage(message);

        MessageDTO dto = new MessageDTO();
        dto.setId(saved.getId());
        dto.setConversationId(saved.getConversation().getId());
        dto.setSenderId(saved.getSenderId());
        dto.setContent(saved.getContent());
        dto.setRead(saved.isRead());
        dto.setSentAt(saved.getSentAt());
        return dto;
    }

    // === Participants ===
    @GetMapping("/participants/{userId}")
    public List<ParticipantDTO> getUserConversations(@PathVariable Long userId) {
        return chatService.getParticipantsByUser(userId).stream().map(p -> {
            ParticipantDTO dto = new ParticipantDTO();
            dto.setId(p.getId());
            dto.setConversationId(p.getConversation().getId());
            dto.setUserId(p.getUserId());
            dto.setRole(p.getRole());
            dto.setJoinedAt(p.getJoinedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    @PostMapping("/participants")
    public ParticipantDTO addParticipant(@RequestBody Participant participant) {
        Participant saved = chatService.saveParticipant(participant);
        ParticipantDTO dto = new ParticipantDTO();
        dto.setId(saved.getId());
        dto.setConversationId(saved.getConversation().getId());
        dto.setUserId(saved.getUserId());
        dto.setRole(saved.getRole());
        dto.setJoinedAt(saved.getJoinedAt());
        return dto;
    }
}
