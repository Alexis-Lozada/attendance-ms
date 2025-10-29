package mx.edu.uteq.idgs12.academic_ms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.edu.uteq.idgs12.academic_ms.entity.Division;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Integer> {
    
    List<Division> findByUniversity_IdUniversity(Integer idUniversity);
    
    List<Division> findByStatusTrue();
    
    Optional<Division> findByCodeAndUniversity_IdUniversity(String code, Integer idUniversity);
    
    List<Division> findByUniversity_IdUniversityAndStatusTrue(Integer idUniversity);
    
    boolean existsByCodeAndUniversity_IdUniversity(String code, Integer idUniversity);
}