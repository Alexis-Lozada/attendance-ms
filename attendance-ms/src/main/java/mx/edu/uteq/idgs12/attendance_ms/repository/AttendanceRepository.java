package mx.edu.uteq.idgs12.attendance_ms.repository;

import mx.edu.uteq.idgs12.attendance_ms.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    List<Attendance> findByIdSchedule(Integer idSchedule);

    List<Attendance> findByIdStudent(Integer idStudent);

    List<Attendance> findByIdScheduleAndAttendanceDate(Integer idSchedule, String attendanceDate);

    // 🔹 Obtener asistencias por idGroupCourse usando join con Schedule
    List<Attendance> findByIdScheduleIn(List<Integer> idSchedules);
}
