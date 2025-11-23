package mx.edu.uteq.idgs12.academic_ms.repository;

import mx.edu.uteq.idgs12.academic_ms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    List<Course> findByUniversity_IdUniversity(Integer idUniversity);

    List<Course> findByDivision_IdDivision(Integer idDivision);

    Optional<Course> findByCourseCodeAndUniversity_IdUniversity(String courseCode, Integer idUniversity);

    boolean existsByCourseCodeAndUniversity_IdUniversity(String courseCode, Integer idUniversity);
}
