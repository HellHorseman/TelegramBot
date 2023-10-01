package pro.sky.telegrambot.service;

import java.time.LocalDateTime;

public interface ScheduledTask {

    void checkDatabaseEntries();

    void saveNotification(long chatId, String notificationText, LocalDateTime dateTime);
}
