package mx.edu.uteq.idgs12.academic_ms.controller;

import mx.edu.uteq.idgs12.academic_ms.dto.GroupDTO;
import mx.edu.uteq.idgs12.academic_ms.service.GroupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;
    
    @GetMapping
    public List<GroupDTO> getAll() {
        return groupService.getAll();
    }

    @GetMapping("/active")
    public List<GroupDTO> getAllActive() {
        return groupService.getAllActive();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDTO> getById(@PathVariable Integer id) {
        Optional<GroupDTO> group = groupService.getById(id);
        return group.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Obtener grupos por programa (opcionalmente solo activos) */
    @GetMapping("/program/{idProgram}")
    public ResponseEntity<List<GroupDTO>> getByProgram(
            @PathVariable Integer idProgram,
            @RequestParam(required = false) Boolean active) {
            
        List<GroupDTO> groups = groupService.getByProgram(idProgram, active);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/university/{idUniversity}")
    public List<GroupDTO> getByUniversity(@PathVariable Integer idUniversity) {
        return groupService.getByUniversity(idUniversity);
    }

    @GetMapping("/university/{idUniversity}/active")
    public List<GroupDTO> getActiveByUniversity(@PathVariable Integer idUniversity) {
        return groupService.getActiveByUniversity(idUniversity);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody GroupDTO dto) {
        try {
            GroupDTO saved = groupService.save(dto);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Obtener grupos por división (opcionalmente solo activos) */
    @GetMapping("/division/{idDivision}")
    public ResponseEntity<List<GroupDTO>> getByDivision(
            @PathVariable Integer idDivision,
            @RequestParam(required = false) Boolean active) {

        List<GroupDTO> groups = groupService.getByDivision(idDivision, active);
        return ResponseEntity.ok(groups);
    }

    /** Obtener grupos por tutor */
    @GetMapping("/tutor/{idTutor}")
    public ResponseEntity<List<GroupDTO>> getByTutor(@PathVariable Integer idTutor) {
        List<GroupDTO> groups = groupService.getByTutor(idTutor);
        if (groups.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(groups);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody GroupDTO dto) {
        try {
            dto.setIdGroup(id);
            GroupDTO updated = groupService.save(dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestParam Boolean status) {
        try {
            GroupDTO updated = groupService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            groupService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
