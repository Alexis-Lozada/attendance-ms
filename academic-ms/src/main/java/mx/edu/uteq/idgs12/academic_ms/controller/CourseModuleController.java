package mx.edu.uteq.idgs12.academic_ms.controller;

import mx.edu.uteq.idgs12.academic_ms.dto.CourseModuleDTO;
import mx.edu.uteq.idgs12.academic_ms.service.CourseModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/course-modules")
public class CourseModuleController {

    @Autowired
    private CourseModuleService courseModuleService;

    /** Obtener módulos por curso */
    @GetMapping("/course/{idCourse}")
    public List<CourseModuleDTO> getByCourse(@PathVariable Integer idCourse) {
        return courseModuleService.getByCourse(idCourse);
    }

    /** Obtener un módulo por ID */
    @GetMapping("/{id}")
    public ResponseEntity<CourseModuleDTO> getById(@PathVariable Integer id) {
        Optional<CourseModuleDTO> module = courseModuleService.getById(id);
        return module.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    /** Agregar nuevo módulo */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CourseModuleDTO dto) {
        try {
            CourseModuleDTO saved = courseModuleService.save(dto);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Editar módulo existente */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody CourseModuleDTO dto) {
        try {
            dto.setIdModule(id);
            CourseModuleDTO updated = courseModuleService.save(dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Eliminar módulo */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            courseModuleService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
