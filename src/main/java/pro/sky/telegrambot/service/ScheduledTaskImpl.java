package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.RepositoryNotification;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ScheduledTaskImpl implements ScheduledTask {

    private final TelegramBot telegramBot;

    private final RepositoryNotification repositoryNotification;

    public ScheduledTaskImpl(TelegramBot telegramBot, RepositoryNotification repositoryNotification) {
        this.telegramBot = telegramBot;
        this.repositoryNotification = repositoryNotification;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    @Override
    public void checkDatabaseEntries() {
        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> findTask = repositoryNotification.findByLocalDateTime(localDateTime);

        findTask.forEach(notificationTask -> {
            SendMessage sendMessage = new SendMessage(notificationTask.getChatId(), notificationTask.getText());
            telegramBot.execute(sendMessage);
        });
    }

    @Override
    public void saveNotification(long chatId, String notificationText, LocalDateTime dateTime) {
        NotificationTask notificationTask = new NotificationTask(chatId, notificationText, dateTime);
        repositoryNotification.save(notificationTask);
    }
}
