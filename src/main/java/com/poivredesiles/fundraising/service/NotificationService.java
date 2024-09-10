package com.poivredesiles.fundraising.service;

import com.poivredesiles.fundraising.model.group.PdiCampaign;
import com.poivredesiles.fundraising.model.group.PdiSeller;
import com.poivredesiles.fundraising.model.notification.NotificationSettings;
import com.poivredesiles.fundraising.model.notification.PdiNotification;
import com.poivredesiles.fundraising.model.user.User;
import com.poivredesiles.fundraising.repository.group.PdiCampaignRepository;
import com.poivredesiles.fundraising.repository.group.PdiSellerRepository;
import com.poivredesiles.fundraising.repository.notification.NotificationSettingsRepository;
import com.poivredesiles.fundraising.repository.notification.PdiNotificationRepository;
import com.poivredesiles.fundraising.repository.user.UserRepository;
import com.poivredesiles.fundraising.service.dto.NotificationSettingsDTO;
import com.poivredesiles.fundraising.service.mapper.NotificationSettingsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional
public class NotificationService {

    private final PdiNotificationRepository pdiNotificationRepository;

    private final NotificationSettingsRepository notificationSettingsRepository;

    private final PdiCampaignRepository pdiCampaignRepository;

    private final PdiSellerRepository pdiSellerRepository;

    private final UserRepository userRepository;

    private final MailService mailService;

    private final MessageSource messageSource;

    private final NotificationSettingsMapper notificationSettingsMapper;

    private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(PdiNotificationRepository pdiNotificationRepository,
                               NotificationSettingsRepository notificationSettingsRepository,
                               PdiCampaignRepository pdiCampaignRepository,
                               PdiSellerRepository pdiSellerRepository,
                               UserRepository userRepository,
                               MailService mailService,
                               MessageSource messageSource,
                               NotificationSettingsMapper notificationSettingsMapper) {
        this.pdiNotificationRepository = pdiNotificationRepository;
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.pdiCampaignRepository = pdiCampaignRepository;
        this.pdiSellerRepository = pdiSellerRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.messageSource = messageSource;
        this.notificationSettingsMapper = notificationSettingsMapper;
    }

    // Run everyday at 6:00
    @Scheduled(cron = "0 0 6 * * *")
    public void sendNotifications() {
        log.info("***** >> Sending notifications for ended campaigns << *****");
        List<PdiNotification> notifications = pdiNotificationRepository.findByDateToSendAndDateSentIsNull(LocalDate.now());
        for (PdiNotification notification : notifications) {
            try {
                mailService.sendNotification(notification);
                notification.setDateSent(LocalDateTime.now());
            } catch(Exception e) {
                log.error("Error while sending notification: {}", e.getMessage());
                notification.setDateError(LocalDateTime.now());
                notification.setErrorMessage(e.getMessage());
            }
        }
    }

    // Run everyday at 00:00
    @Scheduled(cron = "0 0 0 * * *")
    public void createNotificationForEndedCampaigns() {
        log.info("***** >> Creating notifications for ended campaigns << *****");
        List<PdiCampaign> pdiCampaigns = pdiCampaignRepository.findByDueDate(LocalDate.now());
        if (pdiCampaigns.isEmpty()) {
            log.info("No campaigns ended today");
            return;
        }
        NotificationSettings notificationSettings = notificationSettingsRepository.findAll().get(0);
        if(!notificationSettings.readyToNotify())
            return;
        for (PdiCampaign campaign : pdiCampaigns) {
            String language = getLeaderLanguage(campaign);
            String subject = messageSource.getMessage("notification.campaign.ended.subject",
                                                        new Object[]{campaign.getProject()},
                                                        Locale.forLanguageTag(language));
            PdiNotification notification = new PdiNotification();
            notification.setDateToSend(LocalDate.now().plusDays(notificationSettings.getNotifyDeadlinePassedDays()));
            notification.setRecipient(campaign.getLeaderEmail());
            notification.setSubject(subject);
            if (language.equals("FR"))
                notification.setMessage(notificationSettings.getNotifyDeadlinePassedMsgFr());
            else
                notification.setMessage(notificationSettings.getNotifyDeadlinePassedMsgEn());
            pdiNotificationRepository.save(notification);
        }
    }

    private String getLeaderLanguage(PdiCampaign campaign) {
        String language = "FR";
        if (!campaign.getLeaderNum().isBlank()) {
            try {
                // Get the user for campaign leader
                Optional<PdiSeller> seller = pdiSellerRepository.findOneByNumber(Long.valueOf(campaign.getLeaderNum()));
                if (seller.isPresent()) {
                    Optional<User> user = userRepository.findById(seller.get().getMe().getId());
                    if (user.isPresent() &&
                            user.get().getLanguage() != null &&
                            !user.get().getLanguage().isBlank()) {
                        language = user.get().getLanguage();
                    }

                }
            }
            catch (Exception e) {
                log.error("Error while getting user for campaign leader: {}", e.getMessage());
            }
        }
        return language;
    }

    public void updateNotificationSettings(NotificationSettingsDTO notificationSettingsDTO) {
        List<NotificationSettings> notificationSettings =notificationSettingsRepository.findAll();
        log.info("Updating notification settings");
        for(NotificationSettings notificationSetting : notificationSettings) {
            notificationSetting.setNotifyDeadlinePassed(notificationSettingsDTO.isNotifyDeadlinePassed());
            notificationSetting.setNotifyDeadlinePassedDays(notificationSettingsDTO.getNotifyDeadlinePassedDays());
            notificationSetting.setNotifyDeadlinePassedMsgEn(notificationSettingsDTO.getNotifyDeadlinePassedMsgEn());
            notificationSetting.setNotifyDeadlinePassedMsgFr(notificationSettingsDTO.getNotifyDeadlinePassedMsgFr());
        }
    }

    public List<NotificationSettingsDTO> getNotificationSettings() {
        List<NotificationSettings> notificationSettings = notificationSettingsRepository.findAll();
        return notificationSettingsMapper.toDto(notificationSettings);
    }
}
