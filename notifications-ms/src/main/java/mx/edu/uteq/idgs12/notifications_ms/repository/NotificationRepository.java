package mx.edu.uteq.idgs12.notifications_ms.repository;

import mx.edu.uteq.idgs12.notifications_ms.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}
