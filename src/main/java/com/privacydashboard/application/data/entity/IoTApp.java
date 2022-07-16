package com.privacydashboard.application.data.entity;

import javax.persistence.*;
//import java.util.Set;

@Entity
@Table(name= "iot_app")
public class IoTApp extends AbstractEntity{
    private String name;
    private String description;
    /*@ManyToMany
    @JoinColumn(name = "controller_id")
    private Set<User> dataControllers;
    private Set<User> DPOs;*/

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
    /*public Set<User> getDataControllers() {
        return dataControllers;
    }
    public void setDataControllers(Set<User> dataControllers) {
        this.dataControllers = dataControllers;
    }
    public Set<User> getDPOs() {
        return DPOs;
    }
    public void setDPOs(Set<User> DPOs) {
        this.DPOs = DPOs;
    }*/
}
