package mx.edu.uteq.idgs12.attendance_ms.repository;

import mx.edu.uteq.idgs12.attendance_ms.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    List<Attendance> findByIdSchedule(Integer idSchedule);

    List<Attendance> findByIdStudent(Integer idStudent);

    List<Attendance> findByIdScheduleAndAttendanceDate(Integer idSchedule, Instant attendanceDate);

    // 🔹 Obtener asistencias por idGroupCourse
    List<Attendance> findByIdScheduleIn(List<Integer> idSchedules);

    // 🔹 Verificar si un estudiante ya marcó asistencia hoy en un schedule
    @Query(value = """
        SELECT * FROM attendances 
        WHERE id_schedule = :idSchedule 
        AND id_student = :idStudent 
        AND DATE(attendance_date AT TIME ZONE 'UTC') = CURRENT_DATE
        """, nativeQuery = true)
    List<Attendance> findTodayAttendance(Integer idSchedule, Integer idStudent);
}
