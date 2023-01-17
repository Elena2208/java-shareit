package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto put(@RequestBody UserDto userDto, @PathVariable long id) {
        return userService.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        userService.delete(id);
    }

    @GetMapping("/{id}")
    public UserDto getId(@PathVariable long id) {
        return userService.getById(id);
    }


}
