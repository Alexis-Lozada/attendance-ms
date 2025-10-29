package mx.edu.uteq.idgs12.chat_ms.controller;

import mx.edu.uteq.idgs12.chat_ms.dto.ConversationDTO;
import mx.edu.uteq.idgs12.chat_ms.dto.MessageDTO;
import mx.edu.uteq.idgs12.chat_ms.dto.ParticipantDTO;
import mx.edu.uteq.idgs12.chat_ms.entity.Conversation;
import mx.edu.uteq.idgs12.chat_ms.entity.Message;
import mx.edu.uteq.idgs12.chat_ms.entity.Participant;
import mx.edu.uteq.idgs12.chat_ms.mapper.ChatMapper;
import mx.edu.uteq.idgs12.chat_ms.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;
    private final ChatMapper chatMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, ChatMapper chatMapper, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.chatMapper = chatMapper;
        this.messagingTemplate = messagingTemplate;
    }

    // =========================================================
    // === CONVERSATIONS ===
    // =========================================================

    @GetMapping("/{userId}")
    public List<ConversationDTO> listConversationsByUser(@PathVariable Long userId) {
        return chatService.getConversationsByUser(userId).stream()
                .map(conv -> chatMapper.toConversationDTO(conv, userId))
                .collect(Collectors.toList());
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ConversationDTO> getConversation(@PathVariable Long id) {
        return chatService.getConversationById(id)
                .map(conv -> chatMapper.toConversationDTO(conv, null))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ConversationDTO createConversation(@RequestBody Conversation conversation) {
        return chatMapper.toConversationDTO(chatService.saveConversation(conversation), null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long id) {
        chatService.deleteConversation(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // === MESSAGES ===
    // =========================================================

    @GetMapping("/messages/{conversationId}")
    public List<MessageDTO> getMessages(@PathVariable Long conversationId) {
        return chatService.getMessagesByConversation(conversationId).stream()
                .map(chatMapper::toMessageDTO)
                .collect(Collectors.toList());
    }

    @PostMapping({"/messages", "/messages/{conversationId}"})
    public MessageDTO sendMessage(
            @PathVariable(required = false) Long conversationId,
            @RequestBody MessageDTO dto) {

        Conversation conversation = Optional.ofNullable(conversationId)
                .flatMap(chatService::getConversationById)
                .orElseGet(() -> chatService.createPrivateConversation(dto.getSenderId(), dto.getReceiverId()));

        Message saved = chatService.saveMessage(new Message(
                null,
                conversation,
                dto.getSenderId(),
                dto.getContent(),
                false,
                LocalDateTime.now()
        ));

        MessageDTO res = chatMapper.toMessageDTO(saved);
        messagingTemplate.convertAndSend("/topic/conversation/" + conversation.getId(), res);
        return res;
    }

    @PatchMapping("/messages/{id}/read")
    public ResponseEntity<MessageDTO> markMessageAsRead(@PathVariable Long id) {
        return chatService.getMessageById(id)
                .map(msg -> {
                    msg.setRead(true);
                    return ResponseEntity.ok(chatMapper.toMessageDTO(chatService.saveMessage(msg)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================================================
    // === PARTICIPANTS ===
    // =========================================================

    @GetMapping("/participants/{userId}")
    public List<ParticipantDTO> getUserConversations(@PathVariable Long userId) {
        return chatService.getParticipantsByUser(userId).stream()
                .map(chatMapper::toParticipantDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/participants")
    public ParticipantDTO addParticipant(@RequestBody Participant participant) {
        return chatMapper.toParticipantDTO(chatService.saveParticipant(participant));
    }
}
