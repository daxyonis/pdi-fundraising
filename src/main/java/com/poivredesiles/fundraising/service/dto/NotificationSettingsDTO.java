package com.poivredesiles.fundraising.service.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class NotificationSettingsDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

    private Long id;

    private boolean notifyDeadlinePassed;

    private Integer notifyDeadlinePassedDays;

    private String notifyDeadlinePassedMsgFr;

    private String notifyDeadlinePassedMsgEn;
}
