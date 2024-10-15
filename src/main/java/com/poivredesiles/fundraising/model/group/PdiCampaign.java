package com.poivredesiles.fundraising.model.group;

import com.poivredesiles.fundraising.converter.StringCryptoConverter;
import com.poivredesiles.fundraising.model.AbstractAuditingEntity;
import com.poivredesiles.fundraising.model.notification.PdiNotification;
import com.poivredesiles.fundraising.model.order.OrderType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Campaign entity\n@author Eva Maciejko
 */
@Entity
@Table(name = "pdicampaign")
@Data
@EqualsAndHashCode(callSuper=false, exclude = {"pdiGroups", "orderType"})
public class PdiCampaign extends AbstractAuditingEntity implements Serializable {

    public static final double DEFAULT_PERCENT_PROFIT = 50;
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    private Long number;
        
    @Column(name = "organization_num")
    private String organizationNum;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "project")
    private String project;

    @Column(name = "leader_num")
    private String leaderNum;

    @Column(name = "leader_email")
    @Convert(converter = StringCryptoConverter.class)
    private String leaderEmail;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "order_type_num")
    private Long orderTypeNum;

    @Column(name = "blocked", columnDefinition = "BIT DEFAULT 0")
    private boolean blocked = false;
    
    @Column(name = "closed", columnDefinition = "BIT DEFAULT 0")
    private boolean closed = false;
       
    @Column(name = "blocked_date")
    private LocalDate blockedDate;

    @Column(name = "closed_date")
    private LocalDate closedDate;
    
    @Column(name = "export_date")
    private LocalDate exportDate;

    @Column(name = "percent_profit")
    private Double percentProfit = Double.valueOf(DEFAULT_PERCENT_PROFIT);
    
    @OneToMany(mappedBy = "pdiCampaign", fetch = FetchType.LAZY)    
    private Set<PdiGroup> pdiGroups = new HashSet<>();  
    
    @ManyToOne    
    private OrderType orderType;

    @OneToMany
    @JoinTable(
            name = "pdicampaign_notifications", // Name of the join table
            joinColumns = @JoinColumn(name = "campaign_id"), // Foreign key for PdiCampaign
            inverseJoinColumns = @JoinColumn(name = "notification_id") // Foreign key for PdiNotification
    )
    private List<PdiNotification> notifications = new ArrayList<>();
    
    public BigDecimal getTotalSales() {
    	return pdiGroups.stream().map(PdiGroup::getTotalSales).reduce(BigDecimal.ZERO, (a,b) -> a.add(b));
    }
    
    public BigDecimal getTotalProfit() {
    	return getTotalSales().multiply(BigDecimal.valueOf(percentProfit/100));
    }
    
    public Long getTotalNumGroups() {
    	return (long)pdiGroups.size();
    }
    
    public Long getTotalNumPaidOrders() {
    	return pdiGroups.stream().collect(Collectors.summingLong(PdiGroup::getNumPaidOrders));
    }
}
