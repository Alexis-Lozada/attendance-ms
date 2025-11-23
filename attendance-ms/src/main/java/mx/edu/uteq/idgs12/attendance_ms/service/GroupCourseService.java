package mx.edu.uteq.idgs12.attendance_ms.service;

import mx.edu.uteq.idgs12.attendance_ms.dto.GroupCourseDTO;
import mx.edu.uteq.idgs12.attendance_ms.entity.GroupCourse;
import mx.edu.uteq.idgs12.attendance_ms.repository.GroupCourseRepository;
import mx.edu.uteq.idgs12.attendance_ms.client.AcademicFeignClient;
import mx.edu.uteq.idgs12.attendance_ms.client.UsersFeignClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupCourseService {

    @Autowired
    private GroupCourseRepository groupCourseRepository;

    @Autowired
    private AcademicFeignClient academicFeignClient;

    @Autowired
    private UsersFeignClient usersFeignClient;

    public List<GroupCourseDTO> getAll() {
        return groupCourseRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<GroupCourseDTO> getById(Integer id) {
        return groupCourseRepository.findById(id)
                .map(this::toDTO);
    }

    public List<GroupCourseDTO> getByGroup(Integer idGroup) {
        return groupCourseRepository.findByIdGroup(idGroup)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<GroupCourseDTO> getByProfessor(Integer idProfessor) {
        return groupCourseRepository.findByIdProfessor(idProfessor)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Long countByCourse(Integer idCourse) {
        return groupCourseRepository.countByIdCourse(idCourse);
    }

    @Transactional
    public GroupCourseDTO save(GroupCourseDTO dto) {
        GroupCourse entity = toEntity(dto);
        GroupCourse saved = groupCourseRepository.save(entity);
        return toDTO(saved);
    }

    @Transactional
    public void delete(Integer id) {
        if (!groupCourseRepository.existsById(id)) {
            throw new RuntimeException("GroupCourse not found with ID: " + id);
        }
        groupCourseRepository.deleteById(id);
    }

    private GroupCourseDTO toDTO(GroupCourse entity) {
        GroupCourseDTO dto = new GroupCourseDTO();
        BeanUtils.copyProperties(entity, dto);

        try {
            /** Obtener datos del grupo desde academic-ms */
            var group = academicFeignClient.getGroupById(entity.getIdGroup());
            if (group != null && group.get("groupCode") != null) {
                dto.setGroupCode((String) group.get("groupCode"));
            }

            /** Obtener datos del curso desde academic-ms */
            var course = academicFeignClient.getCourseById(entity.getIdCourse());
            if (course != null) {
                dto.setCourseCode((String) course.get("courseCode"));
                dto.setCourseName((String) course.get("courseName"));
            }

            /** Obtener nombre completo del profesor desde users-ms */
            var professor = usersFeignClient.getUserById(entity.getIdProfessor());
            if (professor != null) {
                String firstName = (String) professor.get("firstName");
                String lastName = (String) professor.get("lastName");
                dto.setProfessorName(firstName + " " + lastName);
            }

        } catch (Exception e) {
            System.err.println("Error enriqueciendo GroupCourseDTO: " + e.getMessage());
        }

        return dto;
    }

    private GroupCourse toEntity(GroupCourseDTO dto) {
        GroupCourse entity = new GroupCourse();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
