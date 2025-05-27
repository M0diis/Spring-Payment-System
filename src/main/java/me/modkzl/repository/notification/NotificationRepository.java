package me.modkzl.repository.notification;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.modkzl.jooq.public_.tables.NotificationStatus;
import me.modkzl.jooq.public_.tables.records.NotificationStatusRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {

    private final DSLContext dslContext;

    public void save(@NotNull NotificationStatusRecord notificationStatusRecord) {
        dslContext.insertInto(NotificationStatus.NOTIFICATION_STATUS)
                .set(notificationStatusRecord)
                .execute();
    }
}