package com.poivredesiles.fundraising.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A PdiProduct.
 */
@Entity
@Table(name = "pdi_product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PdiProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_number")
    private String productNumber;

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

    @ManyToOne
    @JsonIgnoreProperties(value = "pdiProducts", allowSetters = true)
    private PdiCategory category;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public PdiProduct productNumber(String productNumber) {
        this.productNumber = productNumber;
        return this;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getNameFr() {
        return nameFr;
    }

    public PdiProduct nameFr(String nameFr) {
        this.nameFr = nameFr;
        return this;
    }

    public void setNameFr(String nameFr) {
        this.nameFr = nameFr;
    }

    public String getNameEn() {
        return nameEn;
    }

    public PdiProduct nameEn(String nameEn) {
        this.nameEn = nameEn;
        return this;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getDescriptionFr() {
        return descriptionFr;
    }

    public PdiProduct descriptionFr(String descriptionFr) {
        this.descriptionFr = descriptionFr;
        return this;
    }

    public void setDescriptionFr(String descriptionFr) {
        this.descriptionFr = descriptionFr;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public PdiProduct descriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
        return this;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getWeight() {
        return weight;
    }

    public PdiProduct weight(String weight) {
        this.weight = weight;
        return this;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public PdiCategory getCategory() {
        return category;
    }

    public PdiProduct category(PdiCategory pdiCategory) {
        this.category = pdiCategory;
        return this;
    }

    public void setCategory(PdiCategory pdiCategory) {
        this.category = pdiCategory;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PdiProduct)) {
            return false;
        }
        return id != null && id.equals(((PdiProduct) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PdiProduct{" +
            "id=" + getId() +
            ", productNumber='" + getProductNumber() + "'" +
            ", nameFr='" + getNameFr() + "'" +
            ", nameEn='" + getNameEn() + "'" +
            ", descriptionFr='" + getDescriptionFr() + "'" +
            ", descriptionEn='" + getDescriptionEn() + "'" +
            ", weight='" + getWeight() + "'" +
            "}";
    }
}
