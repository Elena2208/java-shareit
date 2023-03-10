package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody UserDto userDto, @PathVariable long id) {
        return userClient.updateUser(id,userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        userClient.deleteUserById(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getId(@PathVariable long id) {
        return userClient.getUserById(id);
    }
}
