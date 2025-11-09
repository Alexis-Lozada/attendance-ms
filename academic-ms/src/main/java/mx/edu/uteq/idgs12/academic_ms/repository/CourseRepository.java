package mx.edu.uteq.idgs12.academic_ms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.edu.uteq.idgs12.academic_ms.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    List<Course> findByStatusTrue();
    List<Course> findByUniversity_IdUniversity(Integer idUniversity);
    List<Course> findByUniversity_IdUniversityAndStatusTrue(Integer idUniversity);
    List<Course> findByDivision_IdDivision(Integer idDivision);
    List<Course> findByDivision_IdDivisionAndStatusTrue(Integer idDivision);

    boolean existsByCourseCode(String courseCode);
    Optional<Course> findByCourseCode(String courseCode);
}