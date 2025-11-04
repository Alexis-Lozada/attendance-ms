package mx.edu.uteq.idgs12.users_ms.repository;

import mx.edu.uteq.idgs12.users_ms.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByStudent_IdUser(Integer idStudent);
    List<Enrollment> findByIdGroup(Integer idGroup);
    List<Enrollment> findByStatusTrue();
}
