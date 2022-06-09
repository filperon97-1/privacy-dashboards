package com.privacydashboard.application.data.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name= "iot_app")
public class IoTApp extends AbstractEntity{
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "controller_id")
    private User dataController;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public User getDataController() {
        return dataController;
    }
    public void setDataController(User dataController) {
        this.dataController = dataController;
    }


}
