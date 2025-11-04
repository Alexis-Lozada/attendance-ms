// GroupMapper.java (ACTUALIZADO)
package mx.edu.uteq.idgs12.users_ms.mapper;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import mx.edu.uteq.idgs12.users_ms.client.ProgramClient;
import mx.edu.uteq.idgs12.users_ms.dto.GroupDTO;
import mx.edu.uteq.idgs12.users_ms.entity.Group;
import mx.edu.uteq.idgs12.users_ms.entity.User;
import mx.edu.uteq.idgs12.users_ms.repository.UserRepository;

@Component
public class GroupMapper {

    private static final Logger logger = LoggerFactory.getLogger(GroupMapper.class);
    
    private final ProgramClient programClient;
    private final UserRepository userRepository;

    public GroupMapper(ProgramClient programClient, UserRepository userRepository) {
        this.programClient = programClient;
        this.userRepository = userRepository;
    }

    // === Mapeo de Entidad a DTO (SALIDA - ENRIQUECIDO) ===
    public GroupDTO toGroupDTO(Group group) {
        GroupDTO dto = new GroupDTO();
        dto.setIdGroup(group.getIdGroup());
        dto.setGroupCode(group.getGroupCode());
        dto.setGroupName(group.getGroupName());
        dto.setSemester(group.getSemester());
        dto.setAcademicYear(group.getAcademicYear());
        dto.setStatus(group.getStatus());

        // Campos de FK
        dto.setIdProgram(group.getIdProgram());
        dto.setIdTutor(group.getIdTutor());
        
        // **ENRIQUECIMIENTO DE DATOS**
        enrichProgramInfo(dto, group.getIdProgram());
        enrichTutorInfo(dto, group.getIdTutor());

        return dto;
    }

    // === Mapeo de DTO a Entidad (ENTRADA) ===
    public Group toEntity(GroupDTO dto) {
        Group group = new Group();
        group.setIdGroup(dto.getIdGroup());
        group.setIdProgram(dto.getIdProgram());
        group.setIdTutor(dto.getIdTutor());
        group.setGroupCode(dto.getGroupCode());
        group.setGroupName(dto.getGroupName());
        group.setSemester(dto.getSemester());
        group.setAcademicYear(dto.getAcademicYear());
        group.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        return group;
    }
    
    // **ENRIQUECIMIENTO: Datos del Programa desde academic-ms**
    private void enrichProgramInfo(GroupDTO dto, Integer idProgram) {
        if (idProgram == null) {
            dto.setProgramName("Programa no especificado");
            return;
        }
        
        try {
            logger.info("🔍 Buscando programa con ID: {} en academic-ms", idProgram);
            Map<String, Object> programData = programClient.getProgramById(idProgram);
            
            logger.info("📥 Respuesta recibida de academic-ms: {}", programData);
            
            if (programData != null) {
                // Verificar diferentes posibles nombres de campo
                String programName = null;
                
                if (programData.containsKey("programName")) {
                    programName = (String) programData.get("programName");
                } else if (programData.containsKey("name")) {
                    programName = (String) programData.get("name");
                } else if (programData.containsKey("nombre")) {
                    programName = (String) programData.get("nombre");
                } else if (programData.containsKey("programa")) {
                    programName = (String) programData.get("programa");
                }
                
                // Si no encontramos el nombre en campos comunes, mostrar todos los campos disponibles
                if (programName != null && !programName.trim().isEmpty()) {
                    dto.setProgramName(programName);
                    logger.info("✅ Programa encontrado: {}", programName);
                } else {
                    logger.warn("⚠️ Programa con ID {} no tiene nombre en la respuesta. Campos disponibles: {}", 
                            idProgram, programData.keySet());
                    dto.setProgramName("Programa sin nombre (ID: " + idProgram + ") - Campos: " + programData.keySet());
                }
            } else {
                dto.setProgramName("Programa no encontrado (ID: " + idProgram + ") - Respuesta null");
                logger.warn("❌ Programa con ID {} no encontrado en academic-ms - Respuesta null", idProgram);
            }
        } catch (Exception e) {
            String errorMessage = "Error: " + e.getClass().getSimpleName() + " - " + e.getMessage();
            logger.error("🚨 Error al cargar programa con ID {}: {}", idProgram, errorMessage);
            dto.setProgramName("Error: " + e.getClass().getSimpleName());
            
            // Log más detallado para Feign exceptions
            if (e instanceof feign.FeignException) {
                feign.FeignException fe = (feign.FeignException) e;
                logger.error("🔧 Feign Exception - Status: {}, Content: {}", fe.status(), fe.contentUTF8());
            }
        }
    }

    // **ENRIQUECIMIENTO: Datos del Tutor desde users-ms (propio)**
    private void enrichTutorInfo(GroupDTO dto, Long idTutor) {
        if (idTutor == null) {
            dto.setTutorName("Tutor no especificado");
            return;
        }
        
        try {
            logger.info("Buscando tutor con ID: {}", idTutor);
            // Buscar el tutor en la misma base de datos de users-ms
            User tutor = userRepository.findById(idTutor.intValue())
                    .orElse(null);
            
            if (tutor != null) {
                String tutorName = tutor.getFirstName() + " " + tutor.getLastName();
                dto.setTutorName(tutorName);
                logger.info("Tutor encontrado: {}", tutorName);
            } else {
                dto.setTutorName("Tutor no encontrado (ID: " + idTutor + ")");
                logger.warn("Tutor con ID {} no encontrado", idTutor);
            }
        } catch (Exception e) {
            logger.error("Error al cargar tutor con ID {}: {}", idTutor, e.getMessage());
            dto.setTutorName("Tutor ID: " + idTutor);
        }
    }
}