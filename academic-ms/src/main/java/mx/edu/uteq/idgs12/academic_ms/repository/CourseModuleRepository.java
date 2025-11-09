package mx.edu.uteq.idgs12.academic_ms.repository;

import mx.edu.uteq.idgs12.academic_ms.entity.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseModuleRepository extends JpaRepository<CourseModule, Integer> {

    List<CourseModule> findByCourse_IdCourse(Integer idCourse);

    Optional<CourseModule> findByModuleNumberAndCourse_IdCourse(Integer moduleNumber, Integer idCourse);
    boolean existsByModuleNumberAndCourse_IdCourse(Integer moduleNumber, Integer idCourse);
}