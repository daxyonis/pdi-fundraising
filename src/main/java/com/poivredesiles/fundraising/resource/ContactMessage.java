package com.poivredesiles.fundraising.resource;

import lombok.Data;

/**
 * Resource representing a message from a contact
 * @author evita
 *
 */
@Data
public class ContactMessage {
    private String message;

    private Long campaignNumber;

    private String organization;

    private String name;
}
