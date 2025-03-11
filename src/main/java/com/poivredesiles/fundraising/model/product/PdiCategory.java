package com.poivredesiles.fundraising.model.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.poivredesiles.fundraising.model.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A PdiCategory.
 */
@Entity
@Table(name = "pdicategory")
@Data
@EqualsAndHashCode(callSuper=false, exclude = {"pdiProducts"})
public class PdiCategory extends AbstractAuditingEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    private Long number;

    @Column(name = "description_fr")
    private String descriptionFr;

    @Column(name = "description_en")
    private String descriptionEn;

    @Column(name = "unit_price", precision = 21, scale = 2)
    private BigDecimal unitPrice;

    @OneToMany(mappedBy = "category", fetch=FetchType.LAZY)  
    @JsonIgnoreProperties("category")
    private Set<PdiProduct> pdiProducts = new HashSet<>();

 }
