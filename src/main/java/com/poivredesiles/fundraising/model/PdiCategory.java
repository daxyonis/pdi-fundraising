package com.poivredesiles.fundraising.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A PdiCategory.
 */
@Entity
@Table(name = "pdi_category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PdiCategory implements Serializable {

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

    @OneToMany(mappedBy = "category")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<PdiProduct> pdiProducts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumber() {
        return number;
    }

    public PdiCategory number(Long number) {
        this.number = number;
        return this;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getDescriptionFr() {
        return descriptionFr;
    }

    public PdiCategory descriptionFr(String descriptionFr) {
        this.descriptionFr = descriptionFr;
        return this;
    }

    public void setDescriptionFr(String descriptionFr) {
        this.descriptionFr = descriptionFr;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public PdiCategory descriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
        return this;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public PdiCategory unitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Set<PdiProduct> getPdiProducts() {
        return pdiProducts;
    }

    public PdiCategory pdiProducts(Set<PdiProduct> pdiProducts) {
        this.pdiProducts = pdiProducts;
        return this;
    }

    public PdiCategory addPdiProduct(PdiProduct pdiProduct) {
        this.pdiProducts.add(pdiProduct);
        pdiProduct.setCategory(this);
        return this;
    }

    public PdiCategory removePdiProduct(PdiProduct pdiProduct) {
        this.pdiProducts.remove(pdiProduct);
        pdiProduct.setCategory(null);
        return this;
    }

    public void setPdiProducts(Set<PdiProduct> pdiProducts) {
        this.pdiProducts = pdiProducts;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PdiCategory)) {
            return false;
        }
        return id != null && id.equals(((PdiCategory) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PdiCategory{" +
            "id=" + getId() +
            ", number=" + getNumber() +
            ", descriptionFr='" + getDescriptionFr() + "'" +
            ", descriptionEn='" + getDescriptionEn() + "'" +
            ", unitPrice=" + getUnitPrice() +
            "}";
    }
}
