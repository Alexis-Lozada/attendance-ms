package mx.edu.uteq.idgs12.users_ms.repository;

import mx.edu.uteq.idgs12.users_ms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    List<User> findByIdUniversity(Integer idUniversity);
}
