package mx.edu.uteq.idgs12.chat_ms.repository;

import mx.edu.uteq.idgs12.chat_ms.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
