package mx.edu.uteq.idgs12.chat_ms.repository;

import mx.edu.uteq.idgs12.chat_ms.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByUserId(Long userId);
}
