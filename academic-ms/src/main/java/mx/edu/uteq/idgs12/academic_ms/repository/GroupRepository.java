package mx.edu.uteq.idgs12.academic_ms.repository;

import mx.edu.uteq.idgs12.academic_ms.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    List<Group> findByProgram_IdProgram(Integer idProgram);
    List<Group> findByStatusTrue();
    List<Group> findByProgram_IdProgramAndStatusTrue(Integer idProgram);
    boolean existsByGroupCodeAndProgram_IdProgram(String groupCode, Integer idProgram);
    Optional<Group> findByGroupCodeAndProgram_IdProgram(String groupCode, Integer idProgram);
    List<Group> findByProgram_Division_University_IdUniversity(Integer idUniversity);
    List<Group> findByProgram_Division_University_IdUniversityAndStatusTrue(Integer idUniversity);
}
