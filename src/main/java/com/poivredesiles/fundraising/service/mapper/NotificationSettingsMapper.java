package com.poivredesiles.fundraising.service.mapper;

import com.poivredesiles.fundraising.model.notification.NotificationSettings;
import com.poivredesiles.fundraising.service.dto.NotificationSettingsDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {OrderTypeMapper.class})
public interface NotificationSettingsMapper extends EntityMapper<NotificationSettingsDTO, NotificationSettings>{

    NotificationSettingsDTO toDto(NotificationSettings notificationSettings);

    NotificationSettings toEntity(NotificationSettingsDTO notificationSettingsDTO);

    default NotificationSettings fromId(Long id) {
        if (id == null) {
            return null;
        }
        NotificationSettings notificationSettings = new NotificationSettings();
        notificationSettings.setId(id);
        return notificationSettings;
    }
}
