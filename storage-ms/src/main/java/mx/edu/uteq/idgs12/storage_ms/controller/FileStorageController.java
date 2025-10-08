package mx.edu.uteq.idgs12.storage_ms.controller;

import mx.edu.uteq.idgs12.storage_ms.dto.FileDTO;
import mx.edu.uteq.idgs12.storage_ms.entity.FileStorage;
import mx.edu.uteq.idgs12.storage_ms.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileStorageController {

    private final FileStorageService fileService;

    public FileStorageController(FileStorageService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileDTO> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(fileService.uploadFile(file));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<FileStorage> getFile(@PathVariable String uuid) {
        return fileService.getFileByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteFile(@PathVariable String uuid) {
        fileService.deleteFile(uuid);
        return ResponseEntity.noContent().build();
    }
}
