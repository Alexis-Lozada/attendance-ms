package mx.edu.uteq.idgs12.attendance_ms.service;

import mx.edu.uteq.idgs12.attendance_ms.dto.GroupCourseDTO;
import mx.edu.uteq.idgs12.attendance_ms.entity.GroupCourse;
import mx.edu.uteq.idgs12.attendance_ms.repository.GroupCourseRepository;
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

    public List<Integer> getGroupIdsByCourse(Integer idCourse) {
        return groupCourseRepository.findByIdCourse(idCourse)
                .stream()
                .map(GroupCourse::getIdGroup)
                .collect(Collectors.toList());
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
        return dto;
    }

    private GroupCourse toEntity(GroupCourseDTO dto) {
        GroupCourse entity = new GroupCourse();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
