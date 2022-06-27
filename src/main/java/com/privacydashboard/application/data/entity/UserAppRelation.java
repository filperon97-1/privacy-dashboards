package com.privacydashboard.application.data.entity;

import com.privacydashboard.application.data.Role;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_app_relation")
public class UserAppRelation extends AbstractEntity{
    @Type(type = "uuid-char")
    private UUID idUser;
    @Type(type = "uuid-char")
    private UUID idIOTApp;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> consenses;
    private Role role;

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
    public List<String> getConsenses() {
        return consenses;
    }
    public void setConsenses(List<String> consenses) {
        this.consenses = consenses;
    }
    public Role getDataRole() {
        return role;
    }
    public void setDataRole(Role role) {
        this.role = role;
    }
}
