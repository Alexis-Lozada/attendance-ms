package mx.edu.uteq.idgs12.academic_ms.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.uteq.idgs12.academic_ms.dto.CourseDTO;
import mx.edu.uteq.idgs12.academic_ms.entity.Course;
import mx.edu.uteq.idgs12.academic_ms.entity.Division;
import mx.edu.uteq.idgs12.academic_ms.entity.University;
import mx.edu.uteq.idgs12.academic_ms.repository.CourseRepository;
import mx.edu.uteq.idgs12.academic_ms.repository.DivisionRepository;
import mx.edu.uteq.idgs12.academic_ms.repository.UniversityRepository;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UniversityRepository universityRepository;
    
    @Autowired
    private DivisionRepository divisionRepository;

    public List<CourseDTO> getAll() {
        return courseRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CourseDTO> getAllActive() {
        return courseRepository.findByStatusTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<CourseDTO> getById(Integer id) {
        return courseRepository.findById(id).map(this::toDTO);
    }

    public List<CourseDTO> getByUniversity(Integer idUniversity) {
        return courseRepository.findByUniversity_IdUniversity(idUniversity).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CourseDTO> getActiveByUniversity(Integer idUniversity) {
        return courseRepository.findByUniversity_IdUniversityAndStatusTrue(idUniversity).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<CourseDTO> getByDivision(Integer idDivision) {
        return courseRepository.findByDivision_IdDivision(idDivision).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CourseDTO> getActiveByDivision(Integer idDivision) {
        return courseRepository.findByDivision_IdDivisionAndStatusTrue(idDivision).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseDTO save(CourseDTO dto) {
        University university = universityRepository.findById(dto.getIdUniversity())
                .orElseThrow(() -> new RuntimeException("University not found with ID: " + dto.getIdUniversity()));

        Division division = divisionRepository.findById(dto.getIdDivision())
                .orElseThrow(() -> new RuntimeException("Division not found with ID: " + dto.getIdDivision()));

        if (dto.getIdCourse() == null) {
            if (courseRepository.existsByCourseCode(dto.getCode())) {
                throw new RuntimeException("Course code already exists: " + dto.getCode());
            }
        } else {
            Optional<Course> existing = courseRepository.findByCourseCode(dto.getCode());
            if (existing.isPresent() && !existing.get().getIdCourse().equals(dto.getIdCourse())) {
                throw new RuntimeException("Course code already exists: " + dto.getCode());
            }
        }

        Course course = toEntity(dto, university, division);
        Course saved = courseRepository.save(course);
        return toDTO(saved);
    }

    @Transactional
    public CourseDTO updateStatus(Integer id, Boolean status) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
        course.setStatus(status);
        return toDTO(courseRepository.save(course));
    }

    @Transactional
    public void delete(Integer id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
        courseRepository.delete(course);
    }

    private CourseDTO toDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setIdCourse(course.getIdCourse());
        dto.setCode(course.getCourseCode());
        dto.setName(course.getCourseName());
        dto.setSemester(course.getSemester());
        dto.setStatus(course.getStatus());

        if (course.getUniversity() != null) {
            dto.setIdUniversity(course.getUniversity().getIdUniversity());
            dto.setUniversityName(course.getUniversity().getName());
        }
        if (course.getDivision() != null) {
            dto.setIdDivision(course.getDivision().getIdDivision());
            dto.setDivisionName(course.getDivision().getName());
        }
    
        return dto;
    }

    private Course toEntity(CourseDTO dto, University university, Division division) {
        Course course = new Course();
        course.setIdCourse(dto.getIdCourse());
        course.setCourseCode(dto.getCode());
        course.setCourseName(dto.getName());
        course.setSemester(dto.getSemester());
        course.setStatus(dto.getStatus());
        course.setUniversity(university);
        course.setDivision(division);
        return course;
    }
}