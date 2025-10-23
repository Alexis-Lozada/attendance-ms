package mx.edu.uteq.idgs12.academic_ms.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.uteq.idgs12.academic_ms.dto.DivisionDTO;
import mx.edu.uteq.idgs12.academic_ms.entity.Division;
import mx.edu.uteq.idgs12.academic_ms.entity.University;
import mx.edu.uteq.idgs12.academic_ms.repository.DivisionRepository;
import mx.edu.uteq.idgs12.academic_ms.repository.UniversityRepository;

@Service
public class DivisionService {

    private final DivisionRepository divisionRepository;
    private final UniversityRepository universityRepository;

    public DivisionService(DivisionRepository divisionRepository, UniversityRepository universityRepository) {
        this.divisionRepository = divisionRepository;
        this.universityRepository = universityRepository;
    }

    public List<Division> getAll() {
        return divisionRepository.findAll();
    }

    public List<Division> getAllActive() {
        return divisionRepository.findByStatusTrue();
    }

    public Optional<Division> getById(Integer id) {
        return divisionRepository.findById(id);
    }

    public List<Division> getByUniversity(Integer idUniversity) {
        return divisionRepository.findByUniversity_IdUniversity(idUniversity);
    }

    public List<Division> getActiveByUniversity(Integer idUniversity) {
        return divisionRepository.findByUniversity_IdUniversityAndStatusTrue(idUniversity);
    }

    @Transactional
    public Division save(DivisionDTO dto) {
        University university = universityRepository.findById(dto.getIdUniversity())
                .orElseThrow(() -> new RuntimeException("University not found with ID: " + dto.getIdUniversity()));

        // Validar código único por universidad
        if (dto.getIdDivision() == null) {
            if (divisionRepository.existsByCodeAndUniversity_IdUniversity(dto.getCode(), dto.getIdUniversity())) {
                throw new RuntimeException("Division code already exists for this university: " + dto.getCode());
            }
        } else {
            Optional<Division> existingDivision = divisionRepository.findByCodeAndUniversity_IdUniversity(dto.getCode(), dto.getIdUniversity());
            if (existingDivision.isPresent() && !existingDivision.get().getIdDivision().equals(dto.getIdDivision())) {
                throw new RuntimeException("Division code already exists for this university: " + dto.getCode());
            }
        }

        Division division = new Division();
        division.setIdDivision(dto.getIdDivision());
        division.setUniversity(university);
        division.setCode(dto.getCode());
        division.setName(dto.getName());
        division.setDescription(dto.getDescription());
        division.setStatus(dto.getStatus() != null ? dto.getStatus() : true);

        return divisionRepository.save(division);
    }

    @Transactional
    public Division updateStatus(Integer id, Boolean status) {
        Division division = divisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Division not found with ID: " + id));
        division.setStatus(status);
        return divisionRepository.save(division);
    }

    @Transactional
    public void delete(Integer id) {
        Division division = divisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Division not found with ID: " + id));
        divisionRepository.delete(division);
    }

    @Transactional
    public void softDelete(Integer id) {
        Division division = divisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Division not found with ID: " + id));
        division.setStatus(false);
        divisionRepository.save(division);
    }
}