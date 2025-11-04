package mx.edu.uteq.idgs12.users_ms.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.uteq.idgs12.users_ms.entity.Group;
import mx.edu.uteq.idgs12.users_ms.repository.GroupRepository;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Optional<Group> getGroupById(Integer id) {
        return groupRepository.findById(id);
    }
    
    // Nuevo método para crear el grupo con validación de unicidad de groupCode
    @Transactional
    public Group saveGroup(Group group) {
        if (group.getIdGroup() == null) {
            if (groupRepository.findByGroupCode(group.getGroupCode()).isPresent()) {
                throw new RuntimeException("Group code '" + group.getGroupCode() + "' already exists.");
            }
        } else {
            Optional<Group> existingGroupWithCode = groupRepository.findByGroupCode(group.getGroupCode());
            if (existingGroupWithCode.isPresent() && !existingGroupWithCode.get().getIdGroup().equals(group.getIdGroup())) {
                throw new RuntimeException("Group code '" + group.getGroupCode() + "' already exists in another group.");
            }
        }
        return groupRepository.save(group);
    }

    // Nuevo método para actualizar el estado
    @Transactional
    public Group updateGroupStatus(Integer id, Boolean status) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found with ID: " + id));
        group.setStatus(status);
        return groupRepository.save(group);
    }

    // Método de eliminación con manejo de excepción
    @Transactional
    public void deleteGroup(Integer id) {
        if (!groupRepository.existsById(id)) {
             throw new RuntimeException("Group not found with ID: " + id);
        }
        groupRepository.deleteById(id);
    }
}