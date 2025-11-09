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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.edu.uteq.idgs12.academic_ms.dto.CourseDTO;
import mx.edu.uteq.idgs12.academic_ms.service.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;
    
    @GetMapping
    public List<CourseDTO> getAll() {
        return courseService.getAll();
    }

    @GetMapping("/active")
    public List<CourseDTO> getAllActive() {
        return courseService.getAllActive();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getById(@PathVariable Integer id) {
        Optional<CourseDTO> course = courseService.getById(id);
        return course.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/university/{idUniversity}")
    public List<CourseDTO> getByUniversity(@PathVariable Integer idUniversity) {
        return courseService.getByUniversity(idUniversity);
    }

    @GetMapping("/university/{idUniversity}/active")
    public List<CourseDTO> getActiveByUniversity(@PathVariable Integer idUniversity) {
        return courseService.getActiveByUniversity(idUniversity);
    }

    @GetMapping("/division/{idDivision}")
    public List<CourseDTO> getByDivision(@PathVariable Integer idDivision) {
        return courseService.getByDivision(idDivision);
    }

    @GetMapping("/division/{idDivision}/active")
    public List<CourseDTO> getActiveByDivision(@PathVariable Integer idDivision) {
        return courseService.getActiveByDivision(idDivision);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CourseDTO dto) {
        try {
            CourseDTO saved = courseService.save(dto);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestParam Boolean status) {
        try {
            CourseDTO updated = courseService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            courseService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}