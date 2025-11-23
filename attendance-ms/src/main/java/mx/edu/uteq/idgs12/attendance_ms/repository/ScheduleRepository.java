package mx.edu.uteq.idgs12.attendance_ms.repository;

import mx.edu.uteq.idgs12.attendance_ms.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    /** Obtiene todos los horarios asociados a una relación group_course específica */
    List<Schedule> findByIdGroupCourse(Integer idGroupCourse);

    /** Elimina todos los horarios asociados a una relación group_course específica */
    void deleteByIdGroupCourse(Integer idGroupCourse);
}
