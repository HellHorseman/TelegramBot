package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

public interface RepositoryNotification extends JpaRepository<NotificationTask, Long> {

    List<NotificationTask> findByLocalDateTime(LocalDateTime localDateTime);
}


