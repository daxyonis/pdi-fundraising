package com.poivredesiles.fundraising.repository.notification;

import com.poivredesiles.fundraising.model.notification.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
}
