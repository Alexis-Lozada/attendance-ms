package mx.edu.uteq.idgs12.storage_ms.service;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import mx.edu.uteq.idgs12.storage_ms.dto.FileDTO;
import mx.edu.uteq.idgs12.storage_ms.entity.FileStorage;
import mx.edu.uteq.idgs12.storage_ms.repository.FileStorageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageService {

    private final FileStorageRepository fileRepository;
    private final Storage storage;
    private final String bucketName = "roster-storage";

    public FileStorageService(FileStorageRepository fileRepository) throws IOException {
        this.fileRepository = fileRepository;

        // ✅ Carga explícita del JSON de credenciales
        this.storage = StorageOptions.newBuilder()
                .setProjectId("roster-474406")
                .setCredentials(ServiceAccountCredentials.fromStream(
                        new FileInputStream("D:/Projects/Roster/attendance-ms/storage-ms/src/keys/roster-474406-464b45aeb0cd.json")
                ))
                .build()
                .getService();
    }

    public FileDTO uploadFile(MultipartFile file) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "-" + file.getOriginalFilename();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        String fileUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);

        FileStorage entity = new FileStorage();
        entity.setUuid(uuid);
        entity.setFileName(fileName);
        entity.setContentType(file.getContentType());
        entity.setUrl(fileUrl);
        fileRepository.save(entity);

        FileDTO dto = new FileDTO();
        dto.setUuid(uuid);
        dto.setFileName(fileName);
        dto.setContentType(file.getContentType());
        dto.setUrl(fileUrl);
        return dto;
    }

    public Optional<FileStorage> getFileByUuid(String uuid) {
        return fileRepository.findByUuid(uuid);
    }

    public void deleteFile(String uuid) {
        fileRepository.findByUuid(uuid).ifPresent(file -> {
            storage.delete(bucketName, file.getFileName());
            file.setStatus(false);
            fileRepository.save(file);
        });
    }
}
