package mx.edu.uteq.idgs12.storage_ms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class FileStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFile;

    @Column(nullable = false, unique = true, length = 36)
    private String uuid = UUID.randomUUID().toString();

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(length = 100)
    private String contentType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean status = true;
}
