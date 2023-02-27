package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;
    @NotEmpty
    @NotBlank
    private String  name;
    @Email
    @NotEmpty
    @NotBlank
    private String email;


}
