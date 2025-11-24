package mx.edu.uteq.idgs12.attendance_ms.service;

import mx.edu.uteq.idgs12.attendance_ms.client.AcademicFeignClient;
import mx.edu.uteq.idgs12.attendance_ms.client.EnrollmentFeignClient;
import mx.edu.uteq.idgs12.attendance_ms.client.NotificationsFeignClient;
import mx.edu.uteq.idgs12.attendance_ms.dto.EnrollmentDTO;
import mx.edu.uteq.idgs12.attendance_ms.dto.NotificationDTO;
import mx.edu.uteq.idgs12.attendance_ms.entity.AttendanceSession;
import mx.edu.uteq.idgs12.attendance_ms.entity.GroupCourse;
import mx.edu.uteq.idgs12.attendance_ms.repository.AttendanceSessionRepository;
import mx.edu.uteq.idgs12.attendance_ms.repository.GroupCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
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

    /**
     * Inicia un pase de lista, guarda la sesión y notifica a los estudiantes por correo con plantilla HTML.
     */
    @Transactional
    public AttendanceSession startSession(AttendanceSession session) {
        session.setStatus("OPEN");
        session.setStartTime(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        sessionRepository.save(session);

        // 1️⃣ Obtener relación grupo-curso
        GroupCourse relation = groupCourseRepository.findById(session.getIdGroupCourse())
                .orElseThrow(() -> new RuntimeException("GroupCourse no encontrado con ID: " + session.getIdGroupCourse()));

        Integer idGroup = relation.getIdGroup();
        Integer idCourse = relation.getIdCourse();

        // 2️⃣ Obtener nombre del curso desde academic-ms
        String courseName = "Curso";
        try {
            Map<String, Object> course = academicFeignClient.getCourseById(idCourse);
            if (course != null && course.containsKey("courseName")) {
                courseName = (String) course.get("courseName");
            }
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo obtener el nombre del curso: " + e.getMessage());
        }

        // 3️⃣ Obtener alumnos inscritos desde users-ms
        List<EnrollmentDTO> enrollments = enrollmentFeignClient.getEnrollmentsByGroup(idGroup);
        if (enrollments.isEmpty()) {
            throw new RuntimeException("No hay estudiantes inscritos en el grupo asociado.");
        }

        // 4️⃣ Enviar correo con plantilla a cada alumno
        for (EnrollmentDTO enrollment : enrollments) {
            String email = enrollment.getStudentEmail();
            String fullName = enrollment.getStudentName();

            if (email == null || fullName == null) continue;

            // ✅ Crear el DTO para el microservicio notifications-ms
            NotificationDTO notification = new NotificationDTO();
            notification.setRecipientEmail(email);
            notification.setSubject("📋 Registro de asistencia – " + courseName);
            notification.setTemplateName("attendance_email_template.html");

            // Variables que se reemplazarán en la plantilla Thymeleaf
            Map<String, Object> vars = new HashMap<>();
            vars.put("studentName", fullName);
            vars.put("courseName", courseName);
            vars.put("attendanceLink", "https://tuapp.com/attendance/mark?groupCourse=" + session.getIdGroupCourse());
            notification.setTemplateVariables(vars);

            try {
                notificationsFeignClient.sendAttendanceEmail(notification);
            } catch (Exception e) {
                System.err.println("⚠️ Error enviando correo a " + email + ": " + e.getMessage());
            }
        }

        return session;
    }

    public List<AttendanceSession> getAllSessions() {
        return sessionRepository.findAll();
    }

    public Optional<AttendanceSession> getById(Integer idSession) {
        return sessionRepository.findById(idSession);
    }
}
