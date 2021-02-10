package com.poivredesiles.fundraising.service.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.domain.PdiCampaign} entity.
 */
public class PdiCampaignDTO implements Serializable {
    
    private Long id;

    private Long number;

    private Long organizationNum;

    private String organizationName;

    private String project;

    private String leaderNum;

    private String leaderEmail;

    private LocalDate dueDate;

    private Integer orderTypeNum;

    private Boolean blocked;

    private Boolean closed;

    private LocalDate blockedDate;

    private LocalDate closedDate;


    private Long orderTypeId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Long getOrganizationNum() {
        return organizationNum;
    }

    public void setOrganizationNum(Long organizationNum) {
        this.organizationNum = organizationNum;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getLeaderNum() {
        return leaderNum;
    }

    public void setLeaderNum(String leaderNum) {
        this.leaderNum = leaderNum;
    }

    public String getLeaderEmail() {
        return leaderEmail;
    }

    public void setLeaderEmail(String leaderEmail) {
        this.leaderEmail = leaderEmail;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getOrderTypeNum() {
        return orderTypeNum;
    }

    public void setOrderTypeNum(Integer orderTypeNum) {
        this.orderTypeNum = orderTypeNum;
    }

    public Boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Boolean isClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public LocalDate getBlockedDate() {
        return blockedDate;
    }

    public void setBlockedDate(LocalDate blockedDate) {
        this.blockedDate = blockedDate;
    }

    public LocalDate getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(LocalDate closedDate) {
        this.closedDate = closedDate;
    }

    public Long getOrderTypeId() {
        return orderTypeId;
    }

    public void setOrderTypeId(Long orderTypeId) {
        this.orderTypeId = orderTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PdiCampaignDTO)) {
            return false;
        }

        return id != null && id.equals(((PdiCampaignDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PdiCampaignDTO{" +
            "id=" + getId() +
            ", number=" + getNumber() +
            ", organizationNum=" + getOrganizationNum() +
            ", organizationName='" + getOrganizationName() + "'" +
            ", project='" + getProject() + "'" +
            ", leaderNum='" + getLeaderNum() + "'" +
            ", leaderEmail='" + getLeaderEmail() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", orderTypeNum=" + getOrderTypeNum() +
            ", blocked='" + isBlocked() + "'" +
            ", closed='" + isClosed() + "'" +
            ", blockedDate='" + getBlockedDate() + "'" +
            ", closedDate='" + getClosedDate() + "'" +
            ", orderTypeId=" + getOrderTypeId() +
            "}";
    }
}
