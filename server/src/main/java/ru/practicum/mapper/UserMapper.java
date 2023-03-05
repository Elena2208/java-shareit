package ru.practicum.mapper;


import ru.practicum.user.User;
import ru.practicum.user.UserDto;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static User toUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static List<UserDto> toListUserDto(List<User> list) {
        return list.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
