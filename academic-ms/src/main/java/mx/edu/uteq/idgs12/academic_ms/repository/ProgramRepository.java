package mx.edu.uteq.idgs12.academic_ms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.edu.uteq.idgs12.academic_ms.entity.Program;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Integer> {
    
    List<Program> findByDivision_IdDivision(Integer idDivision);
    
    List<Program> findByStatusTrue();
    
    List<Program> findByDivision_IdDivisionAndStatusTrue(Integer idDivision);
    
    Optional<Program> findByProgramCodeAndDivision_IdDivision(String programCode, Integer idDivision);
    
    boolean existsByProgramCodeAndDivision_IdDivision(String programCode, Integer idDivision);
    
    List<Program> findByDivision_University_IdUniversity(Integer idUniversity);
    
    List<Program> findByDivision_University_IdUniversityAndStatusTrue(Integer idUniversity);
}