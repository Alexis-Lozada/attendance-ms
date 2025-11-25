package mx.edu.uteq.idgs12.attendance_ms.service;

import mx.edu.uteq.idgs12.attendance_ms.client.AcademicFeignClient;
import mx.edu.uteq.idgs12.attendance_ms.client.EnrollmentFeignClient;
import mx.edu.uteq.idgs12.attendance_ms.client.NotificationsFeignClient;
import mx.edu.uteq.idgs12.attendance_ms.dto.AttendanceSessionDTO;
import mx.edu.uteq.idgs12.attendance_ms.dto.EnrollmentDTO;
import mx.edu.uteq.idgs12.attendance_ms.dto.NotificationDTO;
import mx.edu.uteq.idgs12.attendance_ms.entity.AttendanceSession;
import mx.edu.uteq.idgs12.attendance_ms.entity.GroupCourse;
import mx.edu.uteq.idgs12.attendance_ms.repository.AttendanceSessionRepository;
import mx.edu.uteq.idgs12.attendance_ms.repository.GroupCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class AttendanceSessionService {

    @Autowired
    private AttendanceSessionRepository sessionRepository;

    @Autowired
    private GroupCourseRepository groupCourseRepository;

    @Autowired
    private NotificationsFeignClient notificationsFeignClient;

    @Autowired
    private EnrollmentFeignClient enrollmentFeignClient;

    @Autowired
    private AcademicFeignClient academicFeignClient;

    /** Inicia un pase de lista con un horario seleccionado */
    @Transactional
    public AttendanceSession startSession(AttendanceSessionDTO dto) {
        // Crear entidad
        AttendanceSession session = new AttendanceSession();
        session.setIdGroupCourse(dto.getIdGroupCourse());
        session.setIdSchedule(dto.getIdSchedule());
        session.setIdProfessor(dto.getIdProfessor());
        session.setGeoLatitude(dto.getGeoLatitude());
        session.setGeoLongitude(dto.getGeoLongitude());
        session.setStatus("OPEN");
        session.setStartTime(Instant.now());
        session.setExpiresAt(Instant.now().plus(Duration.ofMinutes(15)));

        sessionRepository.save(session);

        // Validar relación grupo-curso
        GroupCourse relation = groupCourseRepository.findById(dto.getIdGroupCourse())
                .orElseThrow(() ->
                        new RuntimeException("GroupCourse no encontrado con ID: " + dto.getIdGroupCourse()));

        Integer idGroup = relation.getIdGroup();
        Integer idCourse = relation.getIdCourse();

        // Obtener nombre del curso desde academic-ms
        String courseName = "Curso";
        try {
            Map<String, Object> course = academicFeignClient.getCourseById(idCourse);
            if (course != null && course.containsKey("courseName")) {
                courseName = (String) course.get("courseName");
            }
        } catch (Exception e) {
            System.err.println("No se pudo obtener el nombre del curso: " + e.getMessage());
        }

        // Obtener alumnos inscritos desde users-ms
        List<EnrollmentDTO> enrollments = enrollmentFeignClient.getEnrollmentsByGroup(idGroup);
        if (enrollments.isEmpty()) {
            throw new RuntimeException("No hay estudiantes inscritos en el grupo asociado.");
        }

        // Enviar correo a cada alumno
        for (EnrollmentDTO enrollment : enrollments) {
            String email = enrollment.getStudentEmail();
            String fullName = enrollment.getStudentName();

            if (email == null || fullName == null) continue;

            NotificationDTO notification = new NotificationDTO();
            notification.setRecipientEmail(email);
            notification.setSubject("Registro de asistencia – " + courseName);
            notification.setTemplateName("attendance_email_template.html");

            Map<String, Object> vars = new HashMap<>();
            vars.put("studentName", fullName);
            vars.put("courseName", courseName);
            vars.put("attendanceLink",
                    "https://tuapp.com/attendance/mark?groupCourse=" + dto.getIdGroupCourse() +
                            "&schedule=" + dto.getIdSchedule());

            notification.setTemplateVariables(vars);

            try {
                notificationsFeignClient.sendAttendanceEmail(notification);
            } catch (Exception e) {
                System.err.println("Error enviando correo a " + email + ": " + e.getMessage());
            }
        }

        return session;
    }

    /** Obtiene todas las sesiones de pase de lista */
    public List<AttendanceSession> getAllSessions() {
        return sessionRepository.findAll();
    }

    /** Obtiene una sesión de pase de lista por su ID */
    public Optional<AttendanceSession> getById(Integer idSession) {
        return sessionRepository.findById(idSession);
    }

    /** Cierra automáticamente sesiones expiradas (cada 1 minuto) */
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void closeExpiredSessions() {
        Instant now = Instant.now();
        List<AttendanceSession> openSessions = sessionRepository.findByStatus("OPEN");

        for (AttendanceSession session : openSessions) {

            if (session.getExpiresAt() != null && session.getExpiresAt().isBefore(now)) {

                session.setStatus("CLOSED");
                sessionRepository.save(session);

                System.out.println("Sesión cerrada automáticamente: ID = " + session.getIdSession());
            }
        }
    }
}
