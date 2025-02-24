package ru.otus.hw.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.otus.hw.enums.RoleStorage;

import java.util.Collection;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserDtoJs implements UserDetails {
    private long id;

    private String username;

    private String password;

    private String email;

    private boolean enabled;

    private Set<RoleStorage> roles;

    @JsonCreator
    public UserDtoJs(
            @JsonProperty("id") long id,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("email") String email,
            @JsonProperty("enabled") boolean enabled,
            @JsonProperty("roles") Set<RoleStorage> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
