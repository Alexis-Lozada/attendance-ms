package mx.edu.uteq.idgs12.academic_ms.controller;

import mx.edu.uteq.idgs12.academic_ms.dto.UniversityDTO;
import mx.edu.uteq.idgs12.academic_ms.entity.University;
import mx.edu.uteq.idgs12.academic_ms.service.UniversityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/universities")
public class UniversityController {

    private final UniversityService universityService;

    public UniversityController(UniversityService universityService) {
        this.universityService = universityService;
    }

    @GetMapping
    public List<University> getAll() {
        return universityService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<University> getById(@PathVariable Integer id) {
        Optional<University> university = universityService.getById(id);
        return university.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<University> create(@RequestBody UniversityDTO dto) {
        return ResponseEntity.ok(universityService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<University> update(@PathVariable Integer id, @RequestBody UniversityDTO dto) {
        dto.setIdUniversity(id);
        return ResponseEntity.ok(universityService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        universityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
