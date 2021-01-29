package com.poivredesiles.fundraising.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.poivredesiles.fundraising.model.order.OrderType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Campaign entity\n@author Eva Maciejko
 */
@Entity
@Table(name = "pdicampaign")
@Data
@EqualsAndHashCode(callSuper=false, exclude = {"pdiGroups", "orderType"})
public class PdiCampaign extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_num")
    private String organizationNum;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "project")
    private String project;

    @Column(name = "leader_num")
    private String leaderNum;

    @Column(name = "leader_email")
    private String leaderEmail;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "order_type_num")
    private Integer orderTypeNum;

    @Column(name = "blocked")
    private boolean blocked = false;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)    
    private Set<PdiGroup> pdiGroups = new HashSet<>();  
    
    @ManyToOne    
    private OrderType orderType;
}
