package com.poivredesiles.fundraising.service.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.domain.PdiGroup} entity.
 */
public class PdiGroupDTO implements Serializable {
	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;

    private Long number;

    private String name;

    private String leaderNum;


    private Long pdiCampaignId;
    
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeaderNum() {
        return leaderNum;
    }

    public void setLeaderNum(String leaderNum) {
        this.leaderNum = leaderNum;
    }

    public Long getPdiCampaignId() {
        return pdiCampaignId;
    }

    public void setPdiCampaignId(Long pdiCampaignId) {
        this.pdiCampaignId = pdiCampaignId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PdiGroupDTO)) {
            return false;
        }

        return id != null && id.equals(((PdiGroupDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PdiGroupDTO{" +
            "id=" + getId() +
            ", number=" + getNumber() +
            ", name='" + getName() + "'" +
            ", leaderNum='" + getLeaderNum() + "'" +
            ", pdiCampaignId=" + getPdiCampaignId() +
            "}";
    }
}
