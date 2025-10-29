package mx.edu.uteq.idgs12.academic_ms.repository;

import mx.edu.uteq.idgs12.academic_ms.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversityRepository extends JpaRepository<University, Integer> {
}
