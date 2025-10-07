package mx.edu.uteq.idgs12.storage_ms.dto;

import lombok.Data;

@Data
public class FileDTO {
    private String uuid;
    private String fileName;
    private String contentType;
    private String url;
}
