package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.exception.NotFoundException;


import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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
    @Mock
    UserRepository repository;

    @BeforeEach
    void create() {
        userDto = new UserDto(0, "user1", "user1@gmail.com");
        userDto = userService.addUser(userDto);
    }

    @Test
    void getAllUsersEmptyList() {
        when(repository.findAll())
                .thenReturn(new ArrayList<>());

        List<User> users = repository.findAll();
        assertEquals(0,users.size());
    }

    @Test
    void addUser() {
        when(repository.save(any()))
                .thenReturn(userDto);
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
    void updateUserNotFound() {
        userService.updateUser(userDto.getId(), userDto);
        Exception ex = assertThrows(NotFoundException.class,
                () -> userService.updateUser(100L, userDto));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getUser() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(new User(1L, "user1", "user1@gmail.com")));
        assertEquals(userDto, userService.getUserById(userDto.getId()));
    }

    @Test
    void getUserNotFound() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(NotFoundException.class, () -> userService.getUserById(10L));
        assertEquals("User not found", e.getMessage());
    }

    @Test
    void getAllUsers() {
        List<UserDto> userList = List.of(userDto);
        assertEquals(userList, userService.getAllUsers());
    }

    @Test
    void deleteUser() {
        userService.deleteUserById(userDto.getId());
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userService.getUserById(userDto.getId()));
        assertEquals("User not found", ex.getMessage());
    }
}