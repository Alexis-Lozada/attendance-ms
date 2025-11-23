package mx.edu.uteq.idgs12.users_ms.controller;

import mx.edu.uteq.idgs12.users_ms.dto.EnrollmentDTO;
import mx.edu.uteq.idgs12.users_ms.service.EnrollmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/student/{idStudent}")
    public List<EnrollmentDTO> getByStudent(@PathVariable Integer idStudent) {
        return enrollmentService.getByStudent(idStudent);
    }

    @GetMapping("/group/{idGroup}")
    public List<EnrollmentDTO> getByGroup(@PathVariable Integer idGroup) {
        return enrollmentService.getByGroup(idGroup);
    }

    @GetMapping("/group/{idGroup}/count")
    public ResponseEntity<Long> getEnrollmentCountByGroup(@PathVariable Integer idGroup) {
        long count = enrollmentService.countByGroup(idGroup);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<EnrollmentDTO> create(@RequestBody EnrollmentDTO dto) {
        return ResponseEntity.ok(enrollmentService.save(dto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EnrollmentDTO> updateStatus(@PathVariable Integer id, @RequestParam Boolean status) {
        return ResponseEntity.ok(enrollmentService.updateStatus(id, status));
    }
}
