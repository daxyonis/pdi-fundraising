package com.poivredesiles.fundraising.model.group;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.poivredesiles.fundraising.converter.StringCryptoConverter;
import com.poivredesiles.fundraising.imports.ImportsUtils;
import com.poivredesiles.fundraising.model.AbstractAuditingEntity;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.model.order.OrderStatusEnum;
import com.poivredesiles.fundraising.model.user.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Seller entity
 * @author Eva Maciejko
 */
@Entity
@Table(name = "pdiseller")
@Data
@EqualsAndHashCode(callSuper=false, exclude= {"me", "buyer", "orderHeaders", "pdiGroup"})
public class PdiSeller extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    private Long number;

    @Column(name = "name")
    @Convert(converter = StringCryptoConverter.class)
    private String name;

    // I, the seller
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(unique = true)
    private User me;

    // A buyer related to this seller 
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(unique = true)
    private User buyer;

    @OneToMany(mappedBy = "pdiSeller")    
    private Set<OrderHeader> orderHeaders = new HashSet<>();

    // Group where this seller sells
    @ManyToOne
    @JsonIgnoreProperties(value = "pdiSellers", allowSetters = true)
    private PdiGroup pdiGroup;

    // Groups that belong to a group leader, that he/she can view
    @OneToMany(mappedBy = "groupLeader")
    private Set<PdiGroup> pdiGroups = new HashSet<>();
    
    public Set<OrderHeader> getOrderHeaders() {
        return orderHeaders;
    }

    public PdiSeller orderHeaders(Set<OrderHeader> orderHeaders) {
        this.orderHeaders = orderHeaders;
        return this;
    }

    public PdiSeller addOrderHeader(OrderHeader orderHeader) {
        this.orderHeaders.add(orderHeader);
        orderHeader.setPdiSeller(this);
        return this;
    }

    public PdiSeller removeOrderHeader(OrderHeader orderHeader) {
        this.orderHeaders.remove(orderHeader);
        orderHeader.setPdiSeller(null);
        return this;
    }

    public void setOrderHeaders(Set<OrderHeader> orderHeaders) {
        this.orderHeaders = orderHeaders;
    }    
   
    public BigDecimal getOrdersTotal() {
    	return orderHeaders.stream()
    			.filter(o -> o.getOrderStatus() == OrderStatusEnum.PAID)
    			.map(OrderHeader::getTotal)
    			.reduce(BigDecimal.ZERO, (a,b) -> a.add(b));
    }
    
    public Long getNumOrders() {
    	return orderHeaders.stream()
    			.filter(o -> o.getOrderStatus() == OrderStatusEnum.PAID)
    			.count();
    }
    
    public boolean isDueDateArrived() {
    	if(this.pdiGroup.getPdiCampaign().getDueDate() != null) {
    		return this.pdiGroup.getPdiCampaign().getDueDate().compareTo(ImportsUtils.convertToLocalDate(Instant.now())) <= 0;
    	} else {
    		return false;
    	}
    }
    
    public Integer getNumGroups() {
    	return Math.max(pdiGroups.size(), 1);
    }
}
