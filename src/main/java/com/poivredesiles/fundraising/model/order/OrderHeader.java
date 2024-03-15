package com.poivredesiles.fundraising.model.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.poivredesiles.fundraising.converter.StringCryptoConverter;
import com.poivredesiles.fundraising.model.AbstractAuditingEntity;
import com.poivredesiles.fundraising.model.group.PdiSeller;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A OrderHeader.
 */
@Entity
@Table(name = "orderheader")
@Data
@EqualsAndHashCode(callSuper=false, exclude = {"orderItems", "pdiSeller"})
public class OrderHeader extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number")
    private Long orderNumber;

    @Column(name = "buyer_name")
    @Convert(converter = StringCryptoConverter.class)
    private String buyerName;

    @Column(name = "buyer_phone")
    @Convert(converter = StringCryptoConverter.class)
    private String buyerPhone;

    @Column(name = "buyer_email")
    @Convert(converter = StringCryptoConverter.class)
    private String buyerEmail;

    @Column(name = "buyer_note")
    private String buyerNote;

    @Column(name = "buyer_language")
    private String buyerLanguage;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatusEnum orderStatus = OrderStatusEnum.PENDING;

    @Column(name = "confirmation_number")
    private String confirmationNumber;

    @Column(name = "confirmation_date")
    private Instant confirmationDate;

    @Column(name = "cancel_date")
    private Instant cancelDate;

    @OneToMany(mappedBy = "header", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})    
    private Set<OrderItem> orderItems = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "orderHeaders", allowSetters = true)
    private PdiSeller pdiSeller;

    @JsonIgnore
    private String payTimestamp;
    
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

	public BigDecimal getTotal() {
		return orderItems.stream().map(oi -> oi.getUnitPrice().multiply(BigDecimal.valueOf(oi.getQuantity()))).reduce(BigDecimal.ZERO, (a,b) -> a.add(b));
	}

	public String getDetail() {
		return orderItems.stream()
                         .map(oi -> oi.getDetail(buyerLanguage).toLowerCase())
                         .reduce("", (a,b) -> a.isEmpty() ? b : (a + "; " + b));
	}

    public String getCampaignName() {
        return this.getPdiSeller().getPdiGroup().getPdiCampaign().getProject();
    }

    public Long getCampaignNumber()  { return this.getPdiSeller().getPdiGroup().getPdiCampaign().getNumber(); }

    public String getOrganizationName() { return this.getPdiSeller().getPdiGroup().getPdiCampaign().getOrganizationName(); }

    public String getSellerName() {
        return this.getPdiSeller().getName();
    }

    public String getGroupName() {
        return this.getPdiSeller().getPdiGroup().getName();
    }

    public String getGroupLeaderName() {
        String name = "";
        if (this.getPdiSeller().getPdiGroup().getGroupLeader() != null) {
            name = this.getPdiSeller().getPdiGroup().getGroupLeader().getName();
        }
        return name;
    }
}
