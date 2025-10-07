package mx.edu.uteq.idgs12.academic_ms.repository;

import mx.edu.uteq.idgs12.academic_ms.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {
    List<Configuration> findByUniversity_IdUniversity(Integer idUniversity);
}
