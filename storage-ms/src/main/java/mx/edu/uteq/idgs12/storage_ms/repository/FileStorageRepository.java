package mx.edu.uteq.idgs12.storage_ms.repository;

import mx.edu.uteq.idgs12.storage_ms.entity.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileStorageRepository extends JpaRepository<FileStorage, Integer> {
    Optional<FileStorage> findByUuid(String uuid);
}
