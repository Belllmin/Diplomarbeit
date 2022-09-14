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
public class RefreshTokenDto {

    //region Fields
    @NotBlank
    private String token;
    private String username;
    //endregion
}