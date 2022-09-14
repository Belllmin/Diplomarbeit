package com.htlleonding.ac.at.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDto {

    //region Fields
    @NotBlank
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    private String password;
    //endregion
}
