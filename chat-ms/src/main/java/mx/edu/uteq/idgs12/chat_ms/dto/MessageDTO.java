package mx.edu.uteq.idgs12.chat_ms.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private Long receiverId;
    private String senderName;
    private String senderAvatar;
    private String content;
    private boolean read;
    private LocalDateTime sentAt;
}
