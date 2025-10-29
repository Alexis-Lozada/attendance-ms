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

import mx.edu.uteq.idgs12.academic_ms.dto.ProgramDTO;
import mx.edu.uteq.idgs12.academic_ms.service.ProgramService;

@RestController
@RequestMapping("/api/programs")
public class ProgramController {

    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @GetMapping
    public List<ProgramDTO> getAll() {
        return programService.getAll();
    }

    @GetMapping("/active")
    public List<ProgramDTO> getAllActive() {
        return programService.getAllActive();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramDTO> getById(@PathVariable Integer id) {
        Optional<ProgramDTO> program = programService.getById(id);
        return program.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/division/{idDivision}")
    public List<ProgramDTO> getByDivision(@PathVariable Integer idDivision) {
        return programService.getByDivision(idDivision);
    }

    @GetMapping("/division/{idDivision}/active")
    public List<ProgramDTO> getActiveByDivision(@PathVariable Integer idDivision) {
        return programService.getActiveByDivision(idDivision);
    }

    @GetMapping("/university/{idUniversity}")
    public List<ProgramDTO> getByUniversity(@PathVariable Integer idUniversity) {
        return programService.getByUniversity(idUniversity);
    }

    @GetMapping("/university/{idUniversity}/active")
    public List<ProgramDTO> getActiveByUniversity(@PathVariable Integer idUniversity) {
        return programService.getActiveByUniversity(idUniversity);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProgramDTO dto) {
        try {
            ProgramDTO savedProgram = programService.save(dto);
            return ResponseEntity.ok(savedProgram);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody ProgramDTO dto) {
        try {
            dto.setIdProgram(id);
            ProgramDTO updatedProgram = programService.save(dto);
            return ResponseEntity.ok(updatedProgram);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestParam Boolean status) {
        try {
            ProgramDTO program = programService.updateStatus(id, status);
            return ResponseEntity.ok(program);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            programService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}