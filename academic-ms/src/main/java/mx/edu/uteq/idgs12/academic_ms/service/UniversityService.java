package mx.edu.uteq.idgs12.academic_ms.service;

import mx.edu.uteq.idgs12.academic_ms.dto.UniversityDTO;
import mx.edu.uteq.idgs12.academic_ms.entity.Configuration;
import mx.edu.uteq.idgs12.academic_ms.entity.University;
import mx.edu.uteq.idgs12.academic_ms.repository.ConfigurationRepository;
import mx.edu.uteq.idgs12.academic_ms.repository.UniversityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UniversityService {

    private final UniversityRepository universityRepository;
    private final ConfigurationRepository configurationRepository;

    public UniversityService(UniversityRepository universityRepository, ConfigurationRepository configurationRepository) {
        this.universityRepository = universityRepository;
        this.configurationRepository = configurationRepository;
    }

    public List<University> getAll() {
        return universityRepository.findAll();
    }

    public Optional<University> getById(Integer id) {
        return universityRepository.findById(id);
    }

    @Transactional
    public University save(UniversityDTO dto) {
        University university = new University();
        university.setIdUniversity(dto.getIdUniversity());
        university.setCode(dto.getCode());
        university.setName(dto.getName());
        university.setCampus(dto.getCampus());
        university.setAddress(dto.getAddress());
        university.setLogo(dto.getLogo());
        university.setEmail(dto.getEmail());
        university.setStatus(dto.getStatus() != null ? dto.getStatus() : true);

        boolean isNew = (dto.getIdUniversity() == null);
        University savedUniversity = universityRepository.save(university);

        if (isNew) {
            Configuration attendanceConfig = new Configuration();
            attendanceConfig.setUniversity(savedUniversity);
            attendanceConfig.setParameterName("min_attendance_percentage");
            attendanceConfig.setParameterValue("85");
            attendanceConfig.setDescription("Minimum attendance percentage required for accreditation");

            Configuration themeColorConfig = new Configuration();
            themeColorConfig.setUniversity(savedUniversity);
            themeColorConfig.setParameterName("theme_primary_color");
            themeColorConfig.setParameterValue("#3B82F6");
            themeColorConfig.setDescription("Primary color of the dashboard theme");

            configurationRepository.save(attendanceConfig);
            configurationRepository.save(themeColorConfig);
        }

        return savedUniversity;
    }

    public void delete(Integer id) {
        universityRepository.deleteById(id);
    }
}
