package ru.otus.hw.models;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    private static final String PREFIX_TO_AUTHORITY = "ROLE_";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    public Role(Role role) {
        this.id = role.getId();
        this.name = role.getName();
    }

    public Role(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return PREFIX_TO_AUTHORITY + name;
    }

}
