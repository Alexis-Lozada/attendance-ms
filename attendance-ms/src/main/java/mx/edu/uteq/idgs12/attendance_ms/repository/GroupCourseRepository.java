package mx.edu.uteq.idgs12.attendance_ms.repository;

import mx.edu.uteq.idgs12.attendance_ms.entity.GroupCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupCourseRepository extends JpaRepository<GroupCourse, Integer> {

    List<GroupCourse> findByIdGroup(Integer idGroup);

    List<GroupCourse> findByIdProfessor(Integer idProfessor);

    List<GroupCourse> findByIdCourse(Integer idCourse);

    Long countByIdCourse(Integer idCourse); 
}
