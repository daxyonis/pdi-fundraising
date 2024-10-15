package com.poivredesiles.fundraising.model.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.poivredesiles.fundraising.model.AbstractAuditingEntity;
import com.poivredesiles.fundraising.model.product.PdiProduct;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A OrderItem.
 */
@Entity
@Table(name = "orderitem")
@Data
@EqualsAndHashCode(callSuper=false, exclude = {"product", "header"})
public class OrderItem extends AbstractAuditingEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number")
    private Long orderNumber;

    @Column(name = "product_number")
    private String productNumber;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "unit_price", precision = 21, scale = 2)
    private BigDecimal unitPrice;

    @OneToOne    
    private PdiProduct product;

    @ManyToOne
    @JsonIgnoreProperties(value = "orderItems", allowSetters = true)
    private OrderHeader header;

    public String getDetail(String language) {
        String detail = quantity.toString();
        if (language.equals("fr")) {
            detail += " x " + product.getNameFr();
            if (product.getFormatFr() != null && !product.getFormatFr().isBlank()) {
                detail += " (" + product.getFormatFr() + ")";
            }
        } else {
            detail += " x " + product.getNameEn();
            if (product.getFormatEn() != null && !product.getFormatEn().isBlank()) {
                detail += " (" + product.getFormatEn() + ")";
            }
        }
        detail += " [" + product.getLabelNumber() + "]";
        return detail;
    }

    public String getNameFr() {
        return product.getNameFr();
    }

    public String getNameEn() {
        return product.getNameEn();
    }

    public String getLabelNumber() {
        return product.getLabelNumber();
    }

    public String getFormatFr() {
            return product.getFormatFr();
    }

    public String getFormatEn() {
        return product.getFormatEn();
    }
    
    public BigDecimal getSubTotal() {
    	return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
