package com.poivredesiles.fundraising.repository.notification;

import com.poivredesiles.fundraising.model.notification.PdiNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PdiNotificationRepository extends JpaRepository<PdiNotification, Long> {

    List<PdiNotification> findByDateToSendAndDateSentIsNull(LocalDate now);
}
