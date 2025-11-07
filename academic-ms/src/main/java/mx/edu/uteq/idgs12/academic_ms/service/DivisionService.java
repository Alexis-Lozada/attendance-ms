package mx.edu.uteq.idgs12.academic_ms.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.uteq.idgs12.academic_ms.dto.DivisionDTO;
import mx.edu.uteq.idgs12.academic_ms.entity.Division;
import mx.edu.uteq.idgs12.academic_ms.entity.University;
import mx.edu.uteq.idgs12.academic_ms.repository.DivisionRepository;
import mx.edu.uteq.idgs12.academic_ms.repository.UniversityRepository;

@Service
public class DivisionService {

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private UniversityRepository universityRepository;

    public List<DivisionDTO> getAll() {
        return divisionRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<DivisionDTO> getAllActive() {
        return divisionRepository.findByStatusTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<DivisionDTO> getById(Integer id) {
        return divisionRepository.findById(id)
                .map(this::toDTO);
    }

    public List<DivisionDTO> getByUniversity(Integer idUniversity) {
        return divisionRepository.findByUniversity_IdUniversity(idUniversity)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<DivisionDTO> getActiveByUniversity(Integer idUniversity) {
        return divisionRepository.findByUniversity_IdUniversityAndStatusTrue(idUniversity)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DivisionDTO save(DivisionDTO dto) {
        // Validar código único por universidad
        if (dto.getIdDivision() == null) {
            if (divisionRepository.existsByCodeAndUniversity_IdUniversity(dto.getCode(), dto.getIdUniversity())) {
                throw new RuntimeException("Division code already exists for this university: " + dto.getCode());
            }
        } else {
            Optional<Division> existingDivision =
                    divisionRepository.findByCodeAndUniversity_IdUniversity(dto.getCode(), dto.getIdUniversity());
            if (existingDivision.isPresent() && !existingDivision.get().getIdDivision().equals(dto.getIdDivision())) {
                throw new RuntimeException("Division code already exists for this university: " + dto.getCode());
            }
        }

        Division division = toEntity(dto);
        Division saved = divisionRepository.save(division);
        return toDTO(saved);
    }

    @Transactional
    public DivisionDTO updateStatus(Integer id, Boolean status) {
        Division division = divisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Division not found with ID: " + id));
        division.setStatus(status);
        return toDTO(divisionRepository.save(division));
    }

    @Transactional
    public void delete(Integer id) {
        Division division = divisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Division not found with ID: " + id));
        divisionRepository.delete(division);
    }

    private DivisionDTO toDTO(Division division) {
        DivisionDTO dto = new DivisionDTO();
        BeanUtils.copyProperties(division, dto);
        dto.setIdUniversity(division.getUniversity().getIdUniversity());
        return dto;
    }

    private Division toEntity(DivisionDTO dto) {
        University university = universityRepository.findById(dto.getIdUniversity())
                .orElseThrow(() -> new RuntimeException("University not found with ID: " + dto.getIdUniversity()));

        Division division = new Division();
        BeanUtils.copyProperties(dto, division);
        division.setUniversity(university);
        division.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        return division;
    }
}
