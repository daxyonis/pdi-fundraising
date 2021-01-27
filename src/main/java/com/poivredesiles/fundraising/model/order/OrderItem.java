package com.poivredesiles.fundraising.model.order;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.poivredesiles.fundraising.model.AbstractAuditingEntity;
import com.poivredesiles.fundraising.model.product.PdiProduct;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A OrderItem.
 */
@Entity
@Table(name = "orderitem")
@Data
@EqualsAndHashCode(callSuper=false)
public class OrderItem extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "product_number")
    private String productNumber;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price", precision = 21, scale = 2)
    private BigDecimal unitPrice;

    @OneToOne
    @JoinColumn(unique = true)
    private PdiProduct product;

    @ManyToOne
    @JsonIgnoreProperties(value = "orderItems", allowSetters = true)
    private OrderHeader header;   
}
