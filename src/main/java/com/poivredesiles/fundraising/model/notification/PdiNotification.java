package com.poivredesiles.fundraising.model.notification;

import com.poivredesiles.fundraising.model.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pdinotification")
@Data
@EqualsAndHashCode(callSuper=false)
public class PdiNotification extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateToSend;
    private LocalDateTime dateSent;
    private String recipient;
    private String subject;
    private String message;
    private LocalDateTime dateError;
    private String errorMessage;

}
