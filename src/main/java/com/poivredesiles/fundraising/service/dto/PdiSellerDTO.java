package com.poivredesiles.fundraising.service.dto;

import java.io.Serializable;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.domain.PdiSeller} entity.
 */
public class PdiSellerDTO implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

    private Long number;

    private String name;


    private Long meId;

    private Long buyerId;

    private Long pdiGroupId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMeId() {
        return meId;
    }

    public void setMeId(Long userId) {
        this.meId = userId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long userId) {
        this.buyerId = userId;
    }

    public Long getPdiGroupId() {
        return pdiGroupId;
    }

    public void setPdiGroupId(Long pdiGroupId) {
        this.pdiGroupId = pdiGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PdiSellerDTO)) {
            return false;
        }

        return id != null && id.equals(((PdiSellerDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PdiSellerDTO{" +
            "id=" + getId() +
            ", number=" + getNumber() +
            ", name='" + getName() + "'" +
            ", meId=" + getMeId() +
            ", buyerId=" + getBuyerId() +
            ", pdiGroupId=" + getPdiGroupId() +
            "}";
    }
}
