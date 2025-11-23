package mx.edu.uteq.idgs12.academic_ms.service;

import mx.edu.uteq.idgs12.academic_ms.dto.CourseModuleDTO;
import mx.edu.uteq.idgs12.academic_ms.entity.Course;
import mx.edu.uteq.idgs12.academic_ms.entity.CourseModule;
import mx.edu.uteq.idgs12.academic_ms.repository.CourseModuleRepository;
import mx.edu.uteq.idgs12.academic_ms.repository.CourseRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseModuleService {

    @Autowired
    private CourseModuleRepository courseModuleRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<CourseModuleDTO> getByCourse(Integer idCourse) {
        return courseModuleRepository.findByCourse_IdCourse(idCourse)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<CourseModuleDTO> getById(Integer id) {
        return courseModuleRepository.findById(id)
                .map(this::toDTO);
    }

    @Transactional
    public CourseModuleDTO save(CourseModuleDTO dto) {
        CourseModule entity = toEntity(dto);
        CourseModule saved = courseModuleRepository.save(entity);
        return toDTO(saved);
    }

    @Transactional
    public void delete(Integer id) {
        if (!courseModuleRepository.existsById(id)) {
            throw new RuntimeException("Module not found with ID: " + id);
        }
        courseModuleRepository.deleteById(id);
    }

    // ==== Helpers ====
    private CourseModuleDTO toDTO(CourseModule entity) {
        CourseModuleDTO dto = new CourseModuleDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setIdCourse(entity.getCourse().getIdCourse());
        return dto;
    }

    private CourseModule toEntity(CourseModuleDTO dto) {
        Course course = courseRepository.findById(dto.getIdCourse())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + dto.getIdCourse()));

        CourseModule entity = new CourseModule();
        BeanUtils.copyProperties(dto, entity);
        entity.setCourse(course);
        return entity;
    }
}
