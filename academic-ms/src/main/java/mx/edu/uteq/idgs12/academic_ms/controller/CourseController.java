package mx.edu.uteq.idgs12.academic_ms.controller;

import mx.edu.uteq.idgs12.academic_ms.dto.CourseDTO;
import mx.edu.uteq.idgs12.academic_ms.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /** Obtener cursos por universidad */
    @GetMapping("/university/{idUniversity}")
    public ResponseEntity<List<CourseDTO>> getByUniversity(
            @PathVariable Integer idUniversity,
            @RequestParam(required = false) Boolean active) {

        List<CourseDTO> courses = courseService.getByUniversity(idUniversity, active);
        return ResponseEntity.ok(courses);
    }

    /** Obtener cursos por división */
    @GetMapping("/division/{idDivision}")
    public ResponseEntity<List<CourseDTO>> getByDivision(
            @PathVariable Integer idDivision,
            @RequestParam(required = false) Boolean active) {

        List<CourseDTO> courses = courseService.getByDivision(idDivision, active);
        return ResponseEntity.ok(courses);
    }

    /** Obtener curso por ID */
    @GetMapping("/{idCourse}")
    public ResponseEntity<CourseDTO> getById(@PathVariable Integer idCourse) {
        Optional<CourseDTO> course = courseService.getById(idCourse);
        return course.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Crear curso */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CourseDTO dto) {
        try {
            CourseDTO saved = courseService.save(dto);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Actualizar curso */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody CourseDTO dto) {
        try {
            dto.setIdCourse(id);
            CourseDTO updated = courseService.save(dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Cambiar estado (activar/desactivar) */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestParam Boolean status) {
        try {
            CourseDTO updated = courseService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
