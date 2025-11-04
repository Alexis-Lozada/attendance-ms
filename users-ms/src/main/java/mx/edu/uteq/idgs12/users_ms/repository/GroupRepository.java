package mx.edu.uteq.idgs12.users_ms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.edu.uteq.idgs12.users_ms.entity.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    
    // Ejemplo de un método de búsqueda por ID de programa
    List<Group> findByIdProgram(Integer idProgram);
    
    // Ejemplo de un método de búsqueda por ID de tutor
    Optional<Group> findByIdTutor(Long idTutor);

    // Busca un grupo por groupCode
    Optional<Group> findByGroupCode(String groupCode);
}