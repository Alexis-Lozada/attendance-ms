package mx.edu.uteq.idgs12.academic_ms.controller;

import java.util.List;
import java.util.Optional;

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

import mx.edu.uteq.idgs12.academic_ms.dto.DivisionDTO;
import mx.edu.uteq.idgs12.academic_ms.entity.Division;
import mx.edu.uteq.idgs12.academic_ms.service.DivisionService;

@RestController
@RequestMapping("/api/divisions")
public class DivisionController {

    private final DivisionService divisionService;

    public DivisionController(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    @GetMapping
    public List<Division> getAll() {
        return divisionService.getAll();
    }

    @GetMapping("/active")
    public List<Division> getAllActive() {
        return divisionService.getAllActive();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Division> getById(@PathVariable Integer id) {
        Optional<Division> division = divisionService.getById(id);
        return division.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/university/{idUniversity}")
    public List<Division> getByUniversity(@PathVariable Integer idUniversity) {
        return divisionService.getByUniversity(idUniversity);
    }

    @GetMapping("/university/{idUniversity}/active")
    public List<Division> getActiveByUniversity(@PathVariable Integer idUniversity) {
        return divisionService.getActiveByUniversity(idUniversity);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody DivisionDTO dto) {
        try {
            Division savedDivision = divisionService.save(dto);
            return ResponseEntity.ok(savedDivision);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody DivisionDTO dto) {
        try {
            // Forzar el ID desde el path variable
            dto.setIdDivision(id);
            
            Division updatedDivision = divisionService.save(dto);
            return ResponseEntity.ok(updatedDivision);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestParam Boolean status) {
        try {
            Division division = divisionService.updateStatus(id, status);
            return ResponseEntity.ok(division);
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

    @DeleteMapping("/{id}/soft")
    public ResponseEntity<?> softDelete(@PathVariable Integer id) {
        try {
            divisionService.softDelete(id);
            return ResponseEntity.ok().body("{\"message\": \"División eliminada correctamente\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}