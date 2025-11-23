package mx.edu.uteq.idgs12.academic_ms.service;

import mx.edu.uteq.idgs12.academic_ms.client.UserClient;
import mx.edu.uteq.idgs12.academic_ms.dto.GroupDTO;
import mx.edu.uteq.idgs12.academic_ms.dto.UserDTO;
import mx.edu.uteq.idgs12.academic_ms.entity.Group;
import mx.edu.uteq.idgs12.academic_ms.entity.Program;
import mx.edu.uteq.idgs12.academic_ms.repository.GroupRepository;
import mx.edu.uteq.idgs12.academic_ms.repository.ProgramRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ProgramRepository programRepository;
    
    @Autowired
    private UserClient userClient;

    public List<GroupDTO> getAll() {
        return groupRepository.findAll()
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<GroupDTO> getAllActive() {
        return groupRepository.findByStatusTrue()
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<GroupDTO> getById(Integer id) {
        return groupRepository.findById(id)
                .map(this::toDTO);
    }

    public List<GroupDTO> getByProgram(Integer idProgram, Boolean active) {
        return groupRepository.findByProgram_IdProgram(idProgram).stream()
                .filter(g -> active == null || !active || Boolean.TRUE.equals(g.getStatus()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<GroupDTO> getByUniversity(Integer idUniversity) {
        return groupRepository.findByProgram_Division_University_IdUniversity(idUniversity)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<GroupDTO> getActiveByUniversity(Integer idUniversity) {
        return groupRepository.findByProgram_Division_University_IdUniversityAndStatusTrue(idUniversity)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<GroupDTO> getByDivision(Integer idDivision, Boolean active) {
        return groupRepository.findByProgram_Division_IdDivision(idDivision).stream()
                .filter(g -> active == null || !active || Boolean.TRUE.equals(g.getStatus()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<GroupDTO> getByTutor(Integer idTutor) {
        return groupRepository.findByIdTutor(idTutor)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupDTO save(GroupDTO dto) {
        Program program = programRepository.findById(dto.getIdProgram())
                .orElseThrow(() -> new RuntimeException("Program not found with ID: " + dto.getIdProgram()));

        // Validar código único por programa
        if (dto.getIdGroup() == null) {
            if (groupRepository.existsByGroupCodeAndProgram_IdProgram(dto.getGroupCode(), dto.getIdProgram())) {
                throw new RuntimeException("Group code already exists for this program: " + dto.getGroupCode());
            }
        } else {
            Optional<Group> existing = groupRepository.findByGroupCodeAndProgram_IdProgram(dto.getGroupCode(), dto.getIdProgram());
            if (existing.isPresent() && !existing.get().getIdGroup().equals(dto.getIdGroup())) {
                throw new RuntimeException("Group code already exists for this program: " + dto.getGroupCode());
            }
        }

        Group group = toEntity(dto, program);
        Group saved = groupRepository.save(group);
        return toDTO(saved);
    }

    @Transactional
    public GroupDTO updateStatus(Integer id, Boolean status) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found with ID: " + id));
        group.setStatus(status);
        return toDTO(groupRepository.save(group));
    }

    @Transactional
    public void delete(Integer id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found with ID: " + id));
        groupRepository.delete(group);
    }

    private GroupDTO toDTO(Group group) {
        GroupDTO dto = new GroupDTO();
        BeanUtils.copyProperties(group, dto);

        dto.setIdProgram(group.getProgram().getIdProgram());
        dto.setProgramName(group.getProgram().getProgramName());

        // Obtener nombre del tutor desde users-ms
        try {
            UserDTO tutor = userClient.getUserById(group.getIdTutor());
            if (tutor != null) {
                dto.setTutorName(tutor.getFirstName() + " " + tutor.getLastName());
            } else {
                dto.setTutorName("Desconocido");
            }
        } catch (Exception e) {
            dto.setTutorName("No disponible");
        }
    
        // Obtener número de inscripciones desde users-ms
        try {
            Long count = userClient.getEnrollmentCountByGroup(group.getIdGroup());
            dto.setEnrollmentCount(count);
        } catch (Exception e) {
            dto.setEnrollmentCount(0L);
        }
    
        return dto;
    }

    private Group toEntity(GroupDTO dto, Program program) {
        Group group = new Group();
        BeanUtils.copyProperties(dto, group);
        group.setProgram(program);
        return group;
    }
}
