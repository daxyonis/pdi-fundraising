package com.poivredesiles.fundraising.model;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.poivredesiles.fundraising.model.order.OrderType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Group entity\n@author Eva Maciejko
 */
@Entity
@Table(name = "pdigroup")
@Data
@EqualsAndHashCode(callSuper=false)
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

    @OneToMany(mappedBy = "group")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Seller> sellers = new HashSet<>();

    @ManyToOne
    private PdiCampaign campaign;

    @ManyToOne
    private OrderType orderType;
}
