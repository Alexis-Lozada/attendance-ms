package mx.edu.uteq.idgs12.attendance_ms.repository;

import mx.edu.uteq.idgs12.attendance_ms.entity.AttendanceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Integer> {

    /** Obtiene la sesión activa (OPEN) actual para un GroupCourse */
    Optional<AttendanceSession> findTopByIdGroupCourseAndStatus(
            Integer idGroupCourse,
            String status
    );

    /** Necesario para el scheduler que cierra sesiones */
    List<AttendanceSession> findByStatus(String status);

    /** Historial de sesiones por grupo-curso y horario */
    List<AttendanceSession> findByIdGroupCourseAndIdScheduleOrderByStartTimeDesc(
            Integer idGroupCourse,
            Integer idSchedule
    );
}
