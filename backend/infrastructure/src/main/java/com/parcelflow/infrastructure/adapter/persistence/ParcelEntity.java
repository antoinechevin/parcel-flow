package com.parcelflow.infrastructure.adapter.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "parcels")
public class ParcelEntity {

    @Id
    private String id;
    private String label;
    private String status;

    public ParcelEntity() {
    }

    public ParcelEntity(String id, String label, String status) {
        this.id = id;
        this.label = label;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
