package com.poivredesiles.fundraising.model.group;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.poivredesiles.fundraising.model.AbstractAuditingEntity;
import com.poivredesiles.fundraising.model.order.OrderType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Group entity\n@author Eva Maciejko
 */
@Entity
@Table(name = "pdigroup")
@Data
@EqualsAndHashCode(callSuper=false, exclude={"pdiSellers", "pdiCampaign", "orderType", "groupLeader"})
public class PdiGroup extends AbstractAuditingEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    private Long number;

    @Column(name = "name")
    private String name;

    @Column(name = "leader_num")
    private String leaderNum;

    @OneToMany(mappedBy = "pdiGroup")
    private Set<PdiSeller> pdiSellers = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "pdiGroups", allowSetters = true)
    private PdiCampaign pdiCampaign;

    @ManyToOne
    @JsonIgnoreProperties(value = "pdiProducts", allowSetters = true)
    private OrderType orderType;
    
    @ManyToOne
    @JsonIgnoreProperties(value = "pdiGroups", allowSetters = true)
    private PdiSeller groupLeader;
    
    public BigDecimal getTotalSales() {
    	return pdiSellers.stream().map(PdiSeller::getOrdersTotal).reduce(BigDecimal.ZERO, (a,b) -> a.add(b));
    }
    
    public Long getNumPaidOrders() {
    	return pdiSellers.stream().collect(Collectors.summingLong(PdiSeller::getNumOrders));
    }
    
    public Long getNumSellers() {
    	return (long) pdiSellers.size();
    }
}
