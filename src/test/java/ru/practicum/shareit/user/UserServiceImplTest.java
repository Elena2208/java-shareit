package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
class UserServiceImplTest {
    @Autowired
    private UserService userService;
    private UserDto userDto;

    @BeforeEach
    void create() {
        userDto = new UserDto(1L, "user1", "user1@gmail.com");
        userDto = userService.addUser(userDto);
    }

    @Test
    void addUser() {
        assertThat(userDto.getName()).isEqualTo("user1");
        assertThat(userDto.getEmail()).isEqualTo("user1@gmail.com");
    }

    @Test
    void updateUser() {
        UserDto updated = new UserDto();
        updated.setEmail("newEmail@gmail.com");
        userDto.setEmail("newEmail@gmail.com");
        assertEquals(userDto, userService.updateUser(userDto.getId(), updated));
    }

    @Test
    void getUser() {
        assertEquals(userDto, userService.getUserById(userDto.getId()));
    }

    @Test
    void getAllUsers() {
        List<UserDto> userList = List.of(userDto);
        assertEquals(userList, userService.getAllUsers());
    }
}
