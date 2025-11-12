package mx.edu.uteq.idgs12.attendance_ms.controller;

import mx.edu.uteq.idgs12.attendance_ms.dto.GroupCourseDTO;
import mx.edu.uteq.idgs12.attendance_ms.service.GroupCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/group-courses")
public class GroupCourseController {

    @Autowired
    private GroupCourseService groupCourseService;

    @GetMapping
    public List<GroupCourseDTO> getAll() {
        return groupCourseService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupCourseDTO> getById(@PathVariable Integer id) {
        Optional<GroupCourseDTO> dto = groupCourseService.getById(id);
        return dto.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/group/{idGroup}")
    public List<GroupCourseDTO> getByGroup(@PathVariable Integer idGroup) {
        return groupCourseService.getByGroup(idGroup);
    }

    @GetMapping("/course/{idCourse}/groups")
    public ResponseEntity<List<Integer>> getGroupIdsByCourse(@PathVariable Integer idCourse) {
        List<Integer> groupIds = groupCourseService.getGroupIdsByCourse(idCourse);
        return ResponseEntity.ok(groupIds);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody GroupCourseDTO dto) {
        try {
            GroupCourseDTO saved = groupCourseService.save(dto);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody GroupCourseDTO dto) {
        try {
            dto.setIdGroupCourse(id);
            GroupCourseDTO updated = groupCourseService.save(dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            groupCourseService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
