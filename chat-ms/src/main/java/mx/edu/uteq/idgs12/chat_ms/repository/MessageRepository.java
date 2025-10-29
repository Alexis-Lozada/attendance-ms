package mx.edu.uteq.idgs12.chat_ms.repository;

import mx.edu.uteq.idgs12.chat_ms.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderBySentAtAsc(Long conversationId);
}
