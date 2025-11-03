package mx.edu.uteq.idgs12.academic_ms.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.edu.uteq.idgs12.academic_ms.dto.ProgramDTO;
import mx.edu.uteq.idgs12.academic_ms.entity.Division;
import mx.edu.uteq.idgs12.academic_ms.entity.Program;
import mx.edu.uteq.idgs12.academic_ms.repository.DivisionRepository;
import mx.edu.uteq.idgs12.academic_ms.repository.ProgramRepository;

@Service
public class ProgramService {

    private final ProgramRepository programRepository;
    private final DivisionRepository divisionRepository;

    public ProgramService(ProgramRepository programRepository, DivisionRepository divisionRepository) {
        this.programRepository = programRepository;
        this.divisionRepository = divisionRepository;
    }

    public List<ProgramDTO> getAll() { // Devuelve DTO
        return programRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProgramDTO> getAllActive() { // Devuelve DTO
        return programRepository.findByStatusTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProgramDTO> getById(Integer id) { // Devuelve DTO
        return programRepository.findById(id)
                .map(this::toDTO);
    }

    public List<ProgramDTO> getByDivision(Integer idDivision) { // Devuelve DTO
        return programRepository.findByDivision_IdDivision(idDivision)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProgramDTO> getActiveByDivision(Integer idDivision) { // Devuelve DTO
        return programRepository.findByDivision_IdDivisionAndStatusTrue(idDivision)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProgramDTO> getByUniversity(Integer idUniversity) { // Devuelve DTO
        return programRepository.findByDivision_University_IdUniversity(idUniversity)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProgramDTO> getActiveByUniversity(Integer idUniversity) { // Devuelve DTO
        return programRepository.findByDivision_University_IdUniversityAndStatusTrue(idUniversity)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

@Transactional
    public ProgramDTO save(ProgramDTO dto) { // Devuelve DTO
        Division division = divisionRepository.findById(dto.getIdDivision())
                .orElseThrow(() -> new RuntimeException("Division not found with ID: " + dto.getIdDivision()));

        // Validar código único por división (La lógica de validación se mantiene)
        if (dto.getIdProgram() == null) {
            if (programRepository.existsByProgramCodeAndDivision_IdDivision(dto.getProgramCode(), dto.getIdDivision())) {
                throw new RuntimeException("Program code already exists for this division: " + dto.getProgramCode());
            }
        } else {
            Optional<Program> existingProgram = programRepository.findByProgramCodeAndDivision_IdDivision(
                    dto.getProgramCode(), dto.getIdDivision());
            if (existingProgram.isPresent() && !existingProgram.get().getIdProgram().equals(dto.getIdProgram())) {
                // Esta es la excepción que se lanza si *otro* programa ya tiene ese código.
                throw new RuntimeException("Program code already exists for this division: " + dto.getProgramCode());
            }
        }

        // Conversión DTO a Entidad
        Program program = toEntity(dto, division);
        Program saved = programRepository.save(program);
        return toDTO(saved); // Devuelve DTO
    }

    @Transactional
    public ProgramDTO updateStatus(Integer id, Boolean status) { // Devuelve DTO
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found with ID: " + id));
        program.setStatus(status);
        return toDTO(programRepository.save(program)); // Devuelve DTO
    }

    @Transactional
    public void delete(Integer id) { // Se mantiene como void
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found with ID: " + id));
        programRepository.delete(program);
    }

    @Transactional
    public ProgramDTO softDelete(Integer id) { // Devuelve DTO
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found with ID: " + id));
        program.setStatus(false);
        return toDTO(programRepository.save(program)); // Devuelve DTO
    }
    
    // Métodos de Conversión (Mappers)
    private ProgramDTO toDTO(Program program) {
        ProgramDTO dto = new ProgramDTO();
        dto.setIdProgram(program.getIdProgram());
        dto.setIdDivision(program.getDivision().getIdDivision());
        dto.setProgramCode(program.getProgramCode());
        dto.setProgramName(program.getProgramName());
        dto.setDescription(program.getDescription());
        dto.setStatus(program.getStatus());
        return dto;
    }

    // Se requiere la entidad Division para crear la entidad Program
    private Program toEntity(ProgramDTO dto, Division division) {
        Program program = new Program();
        program.setIdProgram(dto.getIdProgram());
        program.setDivision(division);
        program.setProgramCode(dto.getProgramCode());
        program.setProgramName(dto.getProgramName());
        program.setDescription(dto.getDescription());
        program.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        return program;
    }
}