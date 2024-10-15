package com.poivredesiles.fundraising.model.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.poivredesiles.fundraising.model.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * A PdiProduct.
 */
@Entity
@Table(name = "pdiproduct")
@Data
@EqualsAndHashCode(callSuper=false, exclude = {"category"})
public class PdiProduct extends AbstractAuditingEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique identifier to PDI
    @Column(name = "product_number")
    private String productNumber;
    
    // Number visible to the customer
    @Column(name = "label_number")
    private String labelNumber;

    @Column(name = "name_fr")
    private String nameFr;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "description_fr")
    private String descriptionFr;

    @Column(name = "description_en")
    private String descriptionEn;

    @Column(name = "weight")
    private String weight;

    @Column(name = "format_fr")
    private String formatFr;

    @Column(name = "format_en")
    private String formatEn;

    @ManyToOne(fetch=FetchType.LAZY)
    @JsonIgnoreProperties("pdiProducts")
    private PdiCategory category;
    
}
