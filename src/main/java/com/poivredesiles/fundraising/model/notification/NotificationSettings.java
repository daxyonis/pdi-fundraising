package com.poivredesiles.fundraising.model.notification;

import com.poivredesiles.fundraising.model.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "notificationsettings")
@Data
@EqualsAndHashCode(callSuper=false)
public class NotificationSettings extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean notifyDeadlinePassed = false;

    private Integer notifyDeadlinePassedDays;

    private String notifyDeadlinePassedMsgFr;

    private String notifyDeadlinePassedMsgEn;

    public boolean readyToNotify() {
        return notifyDeadlinePassed &&
                notifyDeadlinePassedDays != null &&
                notifyDeadlinePassedDays > 0 &&
                notifyDeadlinePassedMsgFr != null &&
                notifyDeadlinePassedMsgEn != null &&
                !notifyDeadlinePassedMsgFr.isEmpty() &&
                !notifyDeadlinePassedMsgEn.isEmpty();
    }
}
