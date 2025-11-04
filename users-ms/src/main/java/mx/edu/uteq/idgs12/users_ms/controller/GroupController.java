package mx.edu.uteq.idgs12.users_ms.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.edu.uteq.idgs12.users_ms.dto.GroupDTO;
import mx.edu.uteq.idgs12.users_ms.entity.Group;
import mx.edu.uteq.idgs12.users_ms.mapper.GroupMapper;
import mx.edu.uteq.idgs12.users_ms.service.GroupService;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "http://localhost:3000") // CORS
public class GroupController {

    private final GroupService groupService;
    private final GroupMapper groupMapper;

    public GroupController(GroupService groupService, GroupMapper groupMapper) {
        this.groupService = groupService;
        this.groupMapper = groupMapper;
    }

    // GET ALL (Existente y Consistente)
    @GetMapping
    public List<GroupDTO> getAll() {
        return groupService.getAllGroups()
                .stream()
                .map(groupMapper::toGroupDTO) // Mapeo a DTO en el Controller
                .collect(Collectors.toList());
    }

    // GET BY ID (Existente y Consistente)
    @GetMapping("/{id}")
    public ResponseEntity<GroupDTO> getById(@PathVariable Integer id) {
        return groupService.getGroupById(id)
                .map(groupMapper::toGroupDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

// POST (CREATE): Vuelve a recibir GroupDTO
    @PostMapping
    public ResponseEntity<?> create(@RequestBody GroupDTO dto) { // <--- RECIBE GroupDTO
        try {
            // Usa el mapper simple
            Group group = groupMapper.toEntity(dto);
            Group saved = groupService.saveGroup(group);
            return ResponseEntity.ok(groupMapper.toGroupDTO(saved));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // PUT (UPDATE): Vuelve a recibir GroupDTO
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody GroupDTO dto) { // <--- RECIBE GroupDTO
        try {
            dto.setIdGroup(id);
            // Usa el mapper simple
            Group group = groupMapper.toEntity(dto);
            Group updated = groupService.saveGroup(group);
            return ResponseEntity.ok(groupMapper.toGroupDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // [UPDATE STATUS y DELETE se mantienen iguales]
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestParam Boolean status) {
        try {
            Group updated = groupService.updateGroupStatus(id, status);
            return ResponseEntity.ok(groupMapper.toGroupDTO(updated));
        } catch (RuntimeException e) {
             if (e.getMessage().contains("Group not found")) {
                return ResponseEntity.notFound().build();
             }
             return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            groupService.deleteGroup(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
}