package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        contains(user);
        validateEmail(user);
        return UserMapper.toUserDto(userStorage.create(user));
    }

    public UserDto update(UserDto userDto, long id) {
        userDto.setId(id);
        User user = UserMapper.toUser(userDto);
        validateEmail(user);
        if (userStorage.getId(user.getId()) == null) {
            throw new NotFoundException("User not found.");
        }
        User updateUser = userStorage.getId(id);
        if (user.getName() != null && !user.getName().isEmpty()) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            updateUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(userStorage.update(updateUser));
    }

    public List<UserDto> getAll() {
        return userStorage.getList()
                .stream()
                .map(u -> UserMapper.toUserDto(u))
                .collect(Collectors.toList());
    }

    public void delete(long id) {
        userStorage.delete(id);
    }

    public UserDto getById(long id) {
        if (userStorage.getId(id) != null) {
            return UserMapper.toUserDto(userStorage.getId(id));
        } else {
            throw new NotFoundException("User not found.");
        }
    }

    private void contains(User user) {
        if (userStorage.getList().stream().anyMatch(us -> us.getName().equals(user.getName())
                || us.getEmail().equals(user.getEmail()))) {
            throw new AlreadyExistsException("The user already exists.");
        }
    }

    private void validateEmail(User user) {
        if (userStorage.getList()
                .stream()
                .anyMatch(
                        st -> st.getEmail().equals(user.getEmail())
                                && st.getId() != user.getId()
                )
        ) {
            throw new AlreadyExistsException("The email is already in use.");
        }
    }
}
