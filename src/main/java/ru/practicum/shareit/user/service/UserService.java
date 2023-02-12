package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


@Service
public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    UserDto getUserById(long userId);

    void deleteUserById(long userId);

    List<UserDto> getAllUsers();
}
