package ru.otus.hw.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String roleName;

    @Override
    public String getAuthority() {
        return roleName;
    }

    public Role() {
    }

    public Role(String roleName) {
        this.roleName = roleName;
    }

    public Role(Role role) {
        this.id = role.getId();
        this.roleName = role.getRoleName();
    }
}
