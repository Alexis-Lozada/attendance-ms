package mx.edu.uteq.idgs12.chat_ms.service;

import mx.edu.uteq.idgs12.chat_ms.entity.Conversation;
import mx.edu.uteq.idgs12.chat_ms.entity.Message;
import mx.edu.uteq.idgs12.chat_ms.entity.Participant;
import mx.edu.uteq.idgs12.chat_ms.repository.ConversationRepository;
import mx.edu.uteq.idgs12.chat_ms.repository.MessageRepository;
import mx.edu.uteq.idgs12.chat_ms.repository.ParticipantRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ParticipantRepository participantRepository;

    public ChatService(ConversationRepository conversationRepository, MessageRepository messageRepository, ParticipantRepository participantRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.participantRepository = participantRepository;
    }

    // === Conversations ===
    public List<Conversation> getConversationsByUser(Long userId) {
        List<Participant> participants = participantRepository.findByUserId(userId);
        return participants.stream()
                .map(Participant::getConversation)
                .distinct()
                .collect(Collectors.toList());
    }

    public Optional<Conversation> getConversationById(Long id) {
        return conversationRepository.findById(id);
    }

    public Conversation saveConversation(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    public void deleteConversation(Long id) {
        conversationRepository.deleteById(id);
    }

    public Optional<Conversation> findPrivateConversationBetween(Long user1, Long user2) {
        List<Conversation> user1Convs = getConversationsByUser(user1);
        return user1Convs.stream()
                .filter(conv -> "PRIVATE".equalsIgnoreCase(conv.getType()) &&
                        conv.getParticipants().stream().anyMatch(p -> p.getUserId().equals(user2)))
                .findFirst();
    }

    public Conversation createPrivateConversation(Long senderId, Long receiverId) {
        return findPrivateConversationBetween(senderId, receiverId)
                .orElseGet(() -> {
                    Conversation c = new Conversation();
                    c.setType("PRIVATE");
                    c.setCreatedAt(LocalDateTime.now());
                    c = saveConversation(c);

                    saveParticipant(new Participant(null, c, senderId, null, LocalDateTime.now()));
                    saveParticipant(new Participant(null, c, receiverId, null, LocalDateTime.now()));

                    return c;
                });
    }

    // === Messages ===
    public List<Message> getMessagesByConversation(Long conversationId) {
        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId);
    }

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }

    // === Participants ===
    public List<Participant> getParticipantsByUser(Long userId) {
        return participantRepository.findByUserId(userId);
    }

    public Participant saveParticipant(Participant participant) {
        return participantRepository.save(participant);
    }
}
