package com.poivredesiles.fundraising.model.order;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.poivredesiles.fundraising.model.AbstractAuditingEntity;
import com.poivredesiles.fundraising.model.product.PdiProduct;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An OrderType (TypeBC)
 * Used to link a PdiGroup with a list of products
 * A PdiGroup will have one OrderType, which in turn will point to many PdiProducts.
 */
@Entity
@Table(name = "ordertype")
@Data
@EqualsAndHashCode(callSuper=false)
public class OrderType extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    private Long number;

//    @OneToMany(mappedBy = "orderType")
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//    private Set<PdiGroup> pdiGroups = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "ordertype_pdiproduct",
               joinColumns = @JoinColumn(name = "ordertype_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "pdiproduct_id", referencedColumnName = "id"))
    private Set<PdiProduct> pdiProducts = new HashSet<>();


    public OrderType addPdiProduct(PdiProduct pdiProduct) {
        this.pdiProducts.add(pdiProduct);
        return this;
    }

    public OrderType removePdiProduct(PdiProduct pdiProduct) {
        this.pdiProducts.remove(pdiProduct);
        return this;
    }
  }
