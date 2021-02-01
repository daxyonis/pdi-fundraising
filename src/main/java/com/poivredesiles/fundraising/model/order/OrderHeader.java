package com.poivredesiles.fundraising.model.order;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.poivredesiles.fundraising.model.AbstractAuditingEntity;
import com.poivredesiles.fundraising.model.PdiSeller;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A OrderHeader.
 */
@Entity
@Table(name = "orderheader")
@Data
@EqualsAndHashCode(callSuper=false)
public class OrderHeader extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "buyer_name")
    private String buyerName;

    @Column(name = "buyer_phone")
    private String buyerPhone;

    @Column(name = "buyer_note")
    private String buyerNote;

    @Column(name = "buyer_language")
    private String buyerLanguage;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatusEnum orderStatus;

    @Column(name = "confirmation_number")
    private String confirmationNumber;

    @OneToMany(mappedBy = "header")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrderItem> orderItems = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "orderHeaders", allowSetters = true)
    private PdiSeller pdiSeller;   

    
    public OrderHeader addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setHeader(this);
        return this;
    }

    public OrderHeader removeOrderItem(OrderItem orderItem) {
        this.orderItems.remove(orderItem);
        orderItem.setHeader(null);
        return this;
    }
}
