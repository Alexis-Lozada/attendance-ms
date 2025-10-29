package mx.edu.uteq.idgs12.chat_ms.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ParticipantDTO {
    private Long id;
    private Long conversationId;
    private Long userId;
    private String role; // OWNER or MEMBER
    private LocalDateTime joinedAt;
}
