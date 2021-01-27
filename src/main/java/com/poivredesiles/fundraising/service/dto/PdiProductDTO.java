package com.poivredesiles.fundraising.service.dto;

import java.io.Serializable;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.model.product.domain.PdiProduct} entity.
 */
public class PdiProductDTO implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

    private String productNumber;

    private String nameFr;

    private String nameEn;

    private String descriptionFr;

    private String descriptionEn;

    private String weight;


    private Long categoryId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getNameFr() {
        return nameFr;
    }

    public void setNameFr(String nameFr) {
        this.nameFr = nameFr;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getDescriptionFr() {
        return descriptionFr;
    }

    public void setDescriptionFr(String descriptionFr) {
        this.descriptionFr = descriptionFr;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long pdiCategoryId) {
        this.categoryId = pdiCategoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PdiProductDTO)) {
            return false;
        }

        return id != null && id.equals(((PdiProductDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PdiProductDTO{" +
            "id=" + getId() +
            ", productNumber='" + getProductNumber() + "'" +
            ", nameFr='" + getNameFr() + "'" +
            ", nameEn='" + getNameEn() + "'" +
            ", descriptionFr='" + getDescriptionFr() + "'" +
            ", descriptionEn='" + getDescriptionEn() + "'" +
            ", weight='" + getWeight() + "'" +
            ", categoryId=" + getCategoryId() +
            "}";
    }
}
