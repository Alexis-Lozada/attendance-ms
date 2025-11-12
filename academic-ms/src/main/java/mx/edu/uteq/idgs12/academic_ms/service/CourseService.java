package mx.edu.uteq.idgs12.academic_ms.service;

import mx.edu.uteq.idgs12.academic_ms.client.AttendanceClient;
import mx.edu.uteq.idgs12.academic_ms.dto.CourseDTO;
import mx.edu.uteq.idgs12.academic_ms.entity.Course;
import mx.edu.uteq.idgs12.academic_ms.entity.Division;
import mx.edu.uteq.idgs12.academic_ms.entity.University;
import mx.edu.uteq.idgs12.academic_ms.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private CourseModuleRepository courseModuleRepository;

    @Autowired
    private AttendanceClient attendanceClient;

    /** Obtener cursos por universidad */
    public List<CourseDTO> getByUniversity(Integer idUniversity, Boolean active) {
        return courseRepository.findByUniversity_IdUniversity(idUniversity).stream()
                .filter(c -> active == null || !active || Boolean.TRUE.equals(c.getStatus()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Obtener cursos por división */
    public List<CourseDTO> getByDivision(Integer idDivision, Boolean active) {
        return courseRepository.findByDivision_IdDivision(idDivision).stream()
                .filter(c -> active == null || !active || Boolean.TRUE.equals(c.getStatus()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Crear o actualizar curso */
    @Transactional
    public CourseDTO save(CourseDTO dto) {
        if (dto.getIdCourse() == null) {
            if (courseRepository.existsByCourseCodeAndUniversity_IdUniversity(dto.getCourseCode(), dto.getIdUniversity())) {
                throw new RuntimeException("Course code already exists for this university: " + dto.getCourseCode());
            }
        } else {
            Optional<Course> existingCourse =
                    courseRepository.findByCourseCodeAndUniversity_IdUniversity(dto.getCourseCode(), dto.getIdUniversity());
            if (existingCourse.isPresent() && !existingCourse.get().getIdCourse().equals(dto.getIdCourse())) {
                throw new RuntimeException("Course code already exists for this university: " + dto.getCourseCode());
            }
        }

        Course course = toEntity(dto);
        Course saved = courseRepository.save(course);
        return toDTO(saved);
    }

    /** Actualizar estado (activar/desactivar) */
    @Transactional
    public CourseDTO updateStatus(Integer id, Boolean status) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
        course.setStatus(status);
        return toDTO(courseRepository.save(course));
    }

    // Helpers
    private CourseDTO toDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        BeanUtils.copyProperties(course, dto);

        dto.setIdUniversity(course.getUniversity().getIdUniversity());

        if (course.getDivision() != null) {
            dto.setIdDivision(course.getDivision().getIdDivision());
            dto.setDivisionCode(course.getDivision().getCode());
            dto.setDivisionName(course.getDivision().getName());
        }

        // Número de módulos del curso
        Long modulesCount = courseModuleRepository.countByCourse_IdCourse(course.getIdCourse());
        dto.setModulesCount(modulesCount != null ? modulesCount : 0L);

        // Obtener grupos relacionados desde attendance-ms
        try {
            List<Integer> groupIds = attendanceClient.getGroupIdsByCourse(course.getIdCourse());
            dto.setGroupIds(groupIds != null ? groupIds : Collections.emptyList());
            dto.setGroupsCount((long) dto.getGroupIds().size());
        } catch (Exception e) {
            dto.setGroupIds(Collections.emptyList());
            dto.setGroupsCount(0L);
            System.err.println("No se pudieron obtener los grupos desde attendance-ms: " + e.getMessage());
        }

        return dto;
    }

    private Course toEntity(CourseDTO dto) {
        University university = universityRepository.findById(dto.getIdUniversity())
                .orElseThrow(() -> new RuntimeException("University not found with ID: " + dto.getIdUniversity()));

        Division division = null;
        if (dto.getIdDivision() != null) {
            division = divisionRepository.findById(dto.getIdDivision())
                    .orElseThrow(() -> new RuntimeException("Division not found with ID: " + dto.getIdDivision()));
        }

        Course course = new Course();
        BeanUtils.copyProperties(dto, course);
        course.setUniversity(university);
        course.setDivision(division);
        course.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        return course;
    }
}
