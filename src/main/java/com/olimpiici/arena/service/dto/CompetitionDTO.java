package com.olimpiici.arena.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Competition entity.
 */
public class CompetitionDTO implements Serializable {

    private Long id;

    private String label;

    private String description;

    private Integer order;

    private Long parentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long competitionId) {
        this.parentId = competitionId;
    }

    
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompetitionDTO competitionDTO = (CompetitionDTO) o;
        if (competitionDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), competitionDTO.getId());
    }

    
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    
    public String toString() {
        return "CompetitionDTO{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", description='" + getDescription() + "'" +
            ", order=" + getOrder() +
            ", parent=" + getParentId() +
            "}";
    }
}
