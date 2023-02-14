package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User oldUser = fromOptionalUser(userId);
        User updateUser = UserMapper.toUser(userDto);
        if (StringUtils.isNotBlank(updateUser.getName())) {
            oldUser.setName(updateUser.getName());
        }
        if (StringUtils.isNotBlank(updateUser.getEmail())) {
            oldUser.setEmail(updateUser.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(oldUser));
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = fromOptionalUser(userId);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toListUserDto(userRepository.findAll());
    }

    private User fromOptionalUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
