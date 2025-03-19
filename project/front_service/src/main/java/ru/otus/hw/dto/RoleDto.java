package ru.otus.hw.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleDto {
    private String roleName;

    @JsonCreator
    public RoleDto(@JsonProperty("roleName") String roleName) {
        this.roleName = roleName;
    }
}
