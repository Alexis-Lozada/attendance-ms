package mx.edu.uteq.idgs12.academic_ms.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mx.edu.uteq.idgs12.academic_ms.dto.DivisionDTO;
import mx.edu.uteq.idgs12.academic_ms.service.DivisionService;

@RestController
@RequestMapping("/api/divisions")
public class DivisionController {

    private final DivisionService divisionService;

    public DivisionController(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    @GetMapping
    public List<DivisionDTO> getAll() {
        return divisionService.getAll();
    }

    @GetMapping("/active")
    public List<DivisionDTO> getAllActive() {
        return divisionService.getAllActive();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DivisionDTO> getById(@PathVariable Integer id) {
        Optional<DivisionDTO> division = divisionService.getById(id);
        return division.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/university/{idUniversity}")
    public List<DivisionDTO> getByUniversity(@PathVariable Integer idUniversity) {
        return divisionService.getByUniversity(idUniversity);
    }

    @GetMapping("/university/{idUniversity}/active")
    public List<DivisionDTO> getActiveByUniversity(@PathVariable Integer idUniversity) {
        return divisionService.getActiveByUniversity(idUniversity);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody DivisionDTO dto) {
        try {
            DivisionDTO saved = divisionService.save(dto);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody DivisionDTO dto) {
        try {
            dto.setIdDivision(id);
            DivisionDTO updated = divisionService.save(dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestParam Boolean status) {
        try {
            DivisionDTO updated = divisionService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            divisionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
