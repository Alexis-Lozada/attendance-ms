package mx.edu.uteq.idgs12.attendance_ms.repository;

import mx.edu.uteq.idgs12.attendance_ms.entity.AttendanceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Integer> {
}
