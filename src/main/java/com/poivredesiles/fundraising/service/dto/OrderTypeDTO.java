package com.poivredesiles.fundraising.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.domain.OrderType} entity.
 */
public class OrderTypeDTO implements Serializable {
    
    private Long id;

    private Long number;

    private Set<PdiProductDTO> productNumbers = new HashSet<>();
    
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

    public Set<PdiProductDTO> getProductNumbers() {
        return productNumbers;
    }

    public void setProductNumbers(Set<PdiProductDTO> pdiProducts) {
        this.productNumbers = pdiProducts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderTypeDTO)) {
            return false;
        }

        return id != null && id.equals(((OrderTypeDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderTypeDTO{" +
            "id=" + getId() +
            ", number=" + getNumber() +
            ", productNumbers='" + getProductNumbers() + "'" +
            "}";
    }
}
