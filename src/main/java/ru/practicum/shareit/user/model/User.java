package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class User {
    private long id;
    @NotEmpty
    @NotBlank
    private String  name;
    @Email
    @NotEmpty
    @NotBlank
    private String email;
}
