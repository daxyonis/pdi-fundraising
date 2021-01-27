package com.poivredesiles.fundraising.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.model.user.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Seller entity\n@author Eva Maciejko
 */
@Entity
@Table(name = "seller")
@Data
@EqualsAndHashCode(callSuper=false)
public class Seller extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    private Long number;

    @Column(name = "name")
    private String name;

    @OneToOne
    @JoinColumn(unique = true)
    private User me;

    @OneToOne
    @JoinColumn(unique = true)
    private User buyer;

    @OneToMany(mappedBy = "seller")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrderHeader> orderHeaders = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "sellers", allowSetters = true)
    private PdiGroup group;

    
    public Set<OrderHeader> getOrderHeaders() {
        return orderHeaders;
    }

    public Seller orderHeaders(Set<OrderHeader> orderHeaders) {
        this.orderHeaders = orderHeaders;
        return this;
    }

    public Seller addOrderHeader(OrderHeader orderHeader) {
        this.orderHeaders.add(orderHeader);
        orderHeader.setSeller(this);
        return this;
    }

    public Seller removeOrderHeader(OrderHeader orderHeader) {
        this.orderHeaders.remove(orderHeader);
        orderHeader.setSeller(null);
        return this;
    }

    public void setOrderHeaders(Set<OrderHeader> orderHeaders) {
        this.orderHeaders = orderHeaders;
    }

    public PdiGroup getGroup() {
        return group;
    }

    public Seller group(PdiGroup pdiGroup) {
        this.group = pdiGroup;
        return this;
    }

    public void setGroup(PdiGroup pdiGroup) {
        this.group = pdiGroup;
    }
   
}
