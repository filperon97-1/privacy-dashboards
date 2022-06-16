package com.privacydashboard.application.data.entity;

import com.privacydashboard.application.data.DataRole;
import com.privacydashboard.application.data.Role;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user_app_relation")
public class UserAppRelation extends AbstractEntity{
    @Type(type = "uuid-char")
    private UUID idUser;
    @Type(type = "uuid-char")
    private UUID idIOTApp;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> consenses;
    private DataRole dataRole;

    public UUID getIdUser() {
        return idUser;
    }
    public void setIdUser(UUID idUser) {
        this.idUser = idUser;
    }
    public UUID getIdIOTApp() {
        return idIOTApp;
    }
    public void setIdIOTApp(UUID idIOTApp) {
        this.idIOTApp = idIOTApp;
    }
    /*public List<String> getConsenses() {
        return consenses;
    }
    public void setConsenses(List<String> consenses) {
        this.consenses = consenses;
    }*/
    public Set<String> getConsenses() {
        return consenses;
    }
    public void setConsenses(Set<String> consenses) {
        this.consenses = consenses;
    }
    public DataRole getDataRole() {
        return dataRole;
    }
    public void setDataRole(DataRole dataRole) {
        this.dataRole = dataRole;
    }
}
