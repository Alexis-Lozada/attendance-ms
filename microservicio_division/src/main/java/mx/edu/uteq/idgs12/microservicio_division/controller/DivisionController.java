package mx.edu.uteq.idgs12.microservicio_division.controller;

import mx.edu.uteq.idgs12.microservicio_division.entity.Division;
import mx.edu.uteq.idgs12.microservicio_division.service.DivisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/divisiones")
public class DivisionController {

    private final DivisionService divisionService;

    public DivisionController(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    @GetMapping
    public List<Division> listar() {
        return divisionService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Division> obtenerPorId(@PathVariable Integer id) {
        return divisionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Division guardar(@RequestBody Division division) {
        return divisionService.guardar(division);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Division> actualizar(@PathVariable Integer id, @RequestBody Division division) {
        return divisionService.obtenerPorId(id)
                .map(d -> {
                    d.setNombre(division.getNombre());
                    return ResponseEntity.ok(divisionService.guardar(d));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (divisionService.obtenerPorId(id).isPresent()) {
            divisionService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}