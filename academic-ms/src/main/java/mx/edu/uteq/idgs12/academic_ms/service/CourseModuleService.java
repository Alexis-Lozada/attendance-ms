package mx.edu.uteq.idgs12.academic_ms.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.uteq.idgs12.academic_ms.dto.CourseModuleDTO;
import mx.edu.uteq.idgs12.academic_ms.entity.Course;
import mx.edu.uteq.idgs12.academic_ms.entity.CourseModule;
import mx.edu.uteq.idgs12.academic_ms.repository.CourseModuleRepository;
import mx.edu.uteq.idgs12.academic_ms.repository.CourseRepository;

@Service
public class CourseModuleService {

    @Autowired
    private CourseModuleRepository moduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<CourseModuleDTO> getAll() {
        return moduleRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<CourseModuleDTO> getById(Integer id) {
        return moduleRepository.findById(id).map(this::toDTO);
    }

    public List<CourseModuleDTO> getByCourse(Integer idCourse) {
        return moduleRepository.findByCourse_IdCourse(idCourse).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseModuleDTO save(CourseModuleDTO dto) {
        Course course = courseRepository.findById(dto.getIdCourse())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + dto.getIdCourse()));

        if (dto.getIdModule() == null) {
            if (moduleRepository.existsByModuleNumberAndCourse_IdCourse(dto.getModuleNumber(), dto.getIdCourse())) {
                throw new RuntimeException("Module number already exists for this course: " + dto.getModuleNumber());
            }
        } else {
            Optional<CourseModule> existing = moduleRepository.findByModuleNumberAndCourse_IdCourse(dto.getModuleNumber(), dto.getIdCourse());
            
            if (existing.isPresent() && !existing.get().getIdModule().equals(dto.getIdModule())) {
                throw new RuntimeException("Module number already exists for this course: " + dto.getModuleNumber());
            }
        }

        CourseModule module = toEntity(dto, course);
        CourseModule saved = moduleRepository.save(module);
        return toDTO(saved);
    }

    @Transactional
    public void delete(Integer id) {
        CourseModule module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course Module not found with ID: " + id));
        moduleRepository.delete(module);
    }

    private CourseModuleDTO toDTO(CourseModule module) {
        CourseModuleDTO dto = new CourseModuleDTO();
        BeanUtils.copyProperties(module, dto);

        if (module.getCourse() != null) {
            dto.setIdCourse(module.getCourse().getIdCourse());
            dto.setCourseName(module.getCourse().getCourseName());
        }
    
        return dto;
    }

    private CourseModule toEntity(CourseModuleDTO dto, Course course) {
        CourseModule module = new CourseModule();
        BeanUtils.copyProperties(dto, module);
        module.setCourse(course);
        return module;
    }
}