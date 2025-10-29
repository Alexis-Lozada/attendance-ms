package mx.edu.uteq.idgs12.academic_ms.controller;

import mx.edu.uteq.idgs12.academic_ms.dto.ConfigurationDTO;
import mx.edu.uteq.idgs12.academic_ms.entity.Configuration;
import mx.edu.uteq.idgs12.academic_ms.service.ConfigurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/configurations")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping
    public List<Configuration> getAll() {
        return configurationService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Configuration> getById(@PathVariable Integer id) {
        Optional<Configuration> config = configurationService.getById(id);
        return config.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/university/{idUniversity}")
    public List<Configuration> getByUniversity(@PathVariable Integer idUniversity) {
        return configurationService.getByUniversity(idUniversity);
    }

    @PostMapping
    public ResponseEntity<Configuration> create(@RequestBody ConfigurationDTO dto) {
        return ResponseEntity.ok(configurationService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Configuration> update(@PathVariable Integer id, @RequestBody ConfigurationDTO dto) {
        dto.setIdConfiguration(id);
        return ResponseEntity.ok(configurationService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        configurationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
