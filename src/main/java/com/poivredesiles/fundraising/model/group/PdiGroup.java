package com.poivredesiles.fundraising.model.group;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.poivredesiles.fundraising.model.AbstractAuditingEntity;
import com.poivredesiles.fundraising.model.order.OrderType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Group entity\n@author Eva Maciejko
 */
@Entity
@Table(name = "pdigroup")
@Data
@EqualsAndHashCode(callSuper=false, exclude={"pdiSellers", "pdiCampaign", "orderType"})
public class PdiGroup extends AbstractAuditingEntity implements Serializable {

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
}
