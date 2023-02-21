package ru.practicum.shareit.itemrequest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
class ItemRequestServiceImplTest {
    @Autowired
    private final ItemRequestService itemRequestService;
    @Autowired
    private final UserService userService;
    private ItemRequestDto itemRequestDto;
    private UserDto requesterDto;

    @BeforeEach
    void prepare() {
        requesterDto = new UserDto(0, "requester", "requestor@gmail.com");
        itemRequestDto = new ItemRequestDto(0, "request description",
                LocalDateTime.now(), new ArrayList<ItemDto>());
    }

    @Test
    void addRequest() {
        requesterDto = userService.addUser(requesterDto);
        itemRequestDto = itemRequestService.addRequest(itemRequestDto, requesterDto.getId());

        assertEquals(itemRequestDto, itemRequestService.getOneRequest(itemRequestDto.getId(), requesterDto.getId()));
    }

    @Test
    void getAllRequestsForRequestor() {
        requesterDto = userService.addUser(requesterDto);
        itemRequestDto = itemRequestService.addRequest(itemRequestDto, requesterDto.getId());
        assertThat(itemRequestService.getAllRequestsForRequester(requesterDto.getId())).hasSize(1)
                .contains(itemRequestDto);
    }

    @Test
    void getAllRequests() {
        requesterDto = userService.addUser(requesterDto);
        itemRequestDto = itemRequestService.addRequest(itemRequestDto, requesterDto.getId());
        List<ItemRequestDto> requests = itemRequestService.getAllRequests(requesterDto.getId(), 0, 2);
        assertThat(requests).hasSize(0);
    }

    @Test
    void testGetAllRequests() {
        requesterDto = userService.addUser(requesterDto);
        itemRequestDto = itemRequestService.addRequest(itemRequestDto, requesterDto.getId());
        List<ItemRequestDto> requests = itemRequestService.getAllRequests();
        assertThat(requests).hasSize(1).contains(itemRequestDto);
    }

    @Test
    void getOneRequest() {
        requesterDto = userService.addUser(requesterDto);
        itemRequestDto = itemRequestService.addRequest(itemRequestDto, requesterDto.getId());
        assertThat(itemRequestDto.getId()).isNotZero();
        assertThat(itemRequestDto.getDescription()).isEqualTo("request description");
        assertThat(itemRequestDto.getItems().size()).isZero();

    }

    @Test
    void getOneRequest_UserNotFound() {
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemRequestService.getOneRequest(requesterDto.getId(), -1));
        assertThat(ex.getMessage()).contains("User not found.");
    }
}