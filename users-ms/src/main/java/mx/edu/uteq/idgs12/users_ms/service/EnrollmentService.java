package mx.edu.uteq.idgs12.users_ms.service;

import mx.edu.uteq.idgs12.users_ms.client.AcademicClient;
import mx.edu.uteq.idgs12.users_ms.dto.EnrollmentDTO;
import mx.edu.uteq.idgs12.users_ms.entity.Enrollment;
import mx.edu.uteq.idgs12.users_ms.entity.User;
import mx.edu.uteq.idgs12.users_ms.repository.EnrollmentRepository;
import mx.edu.uteq.idgs12.users_ms.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AcademicClient academicClient;

    // === Obtener todos los enrollments ===
    public List<EnrollmentDTO> getAll() {
        return enrollmentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // === Obtener por ID de inscripción ===
    public EnrollmentDTO getById(Integer id) {
        return enrollmentRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    // === Obtener por estudiante ===
    public List<EnrollmentDTO> getByStudent(Integer idStudent) {
        return enrollmentRepository.findByStudent_IdUser(idStudent).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // === Obtener por grupo ===
    public List<EnrollmentDTO> getByGroup(Integer idGroup) {
        return enrollmentRepository.findByIdGroup(idGroup).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // === Crear inscripción ===
    public EnrollmentDTO save(EnrollmentDTO dto) {
        User student = userRepository.findById(dto.getIdStudent())
            .orElseThrow(() -> new RuntimeException("Student not found with ID: " + dto.getIdStudent()));

        Enrollment enrollment = new Enrollment();
        BeanUtils.copyProperties(dto, enrollment);
        enrollment.setStudent(student);

        if (enrollment.getEnrollmentDate() == null) enrollment.setEnrollmentDate(LocalDate.now());
        if (enrollment.getStatus() == null) enrollment.setStatus(true);

        Enrollment saved = enrollmentRepository.save(enrollment);
        return toDTO(saved);
    }

    // === Cambiar estado ===
    public EnrollmentDTO updateStatus(Integer id, Boolean status) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        enrollment.setStatus(status);
        return toDTO(enrollmentRepository.save(enrollment));
    }

    // === Eliminar ===
    public void delete(Integer id) {
        enrollmentRepository.deleteById(id);
    }

    // === Mapper Entity → DTO ===
    private EnrollmentDTO toDTO(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        BeanUtils.copyProperties(enrollment, dto);

        dto.setIdStudent(enrollment.getStudent().getIdUser());
        dto.setStudentName(enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName());

        try {
            Map<String, Object> groupData = academicClient.getGroupById(enrollment.getIdGroup());
            dto.setGroupName((String) groupData.getOrDefault("groupName", "No disponible"));
        } catch (Exception e) {
            dto.setGroupName("No disponible");
        }

        return dto;
    }
}
