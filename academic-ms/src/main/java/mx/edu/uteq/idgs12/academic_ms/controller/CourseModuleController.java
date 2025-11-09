package mx.edu.uteq.idgs12.academic_ms.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.edu.uteq.idgs12.academic_ms.dto.CourseModuleDTO;
import mx.edu.uteq.idgs12.academic_ms.service.CourseModuleService;

@RestController
@RequestMapping("/api/course-modules")
public class CourseModuleController {

    @Autowired
    private CourseModuleService moduleService;
    
    @GetMapping
    public List<CourseModuleDTO> getAll() {
        return moduleService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseModuleDTO> getById(@PathVariable Integer id) {
        Optional<CourseModuleDTO> module = moduleService.getById(id);
        return module.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/course/{idCourse}")
    public List<CourseModuleDTO> getByCourse(@PathVariable Integer idCourse) {
        return moduleService.getByCourse(idCourse);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CourseModuleDTO dto) {
        try {
            CourseModuleDTO saved = moduleService.save(dto);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody CourseModuleDTO dto) {
        try {
            dto.setIdModule(id);
            CourseModuleDTO updated = moduleService.save(dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            moduleService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
}