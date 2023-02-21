package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
class ItemServiceImplTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;
    private ItemDto itemDto;
    private UserDto userDto;

    @BeforeEach
    void before() {
        userDto = new UserDto(1L, "user", "user@gmail.com");
        userDto = userService.addUser(userDto);
        itemDto = new ItemDto(1L, "item1", "item description", true, null);
        itemDto = itemService.addItem(itemDto, userDto.getId());
    }

    @Test
    void addItemTest() {
        assertThat(itemDto.getName()).isEqualTo("item1");
        assertThat(itemDto.getDescription()).isEqualTo("item description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isNull();
    }

    @Test
    void updateItem_WithNameTest() {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setName("updated name");
        itemDto.setName("updated name");
        assertEquals(itemDto, itemService.updateItem(updatedItem, itemDto.getId(), userDto.getId()));
    }

    @Test
    void updateItem_WithDescriptionTest() {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setDescription("updated description");
        itemDto.setDescription("updated description");

        assertEquals(itemDto, itemService.updateItem(updatedItem, itemDto.getId(), userDto.getId()));
    }

    @Test
    void updateItem_WithAvailableTest() {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setAvailable(false);
        itemDto.setAvailable(false);

        assertEquals(itemDto, itemService.updateItem(updatedItem, itemDto.getId(), userDto.getId()));
    }

    @Test
    void updateItem_UserNotOwnerTest() {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setName("updated name");
        itemDto.setName("updated name");
        UserDto userNotOwner = new UserDto(0, "notOwner", "notOwner@gmail.com");
        userNotOwner = userService.addUser(userNotOwner);
        long idUserNotOwner = userNotOwner.getId();

        NoAccessException ex = assertThrows(NoAccessException.class,
                () -> itemService.updateItem(updatedItem, itemDto.getId(), idUserNotOwner));
        assertTrue(ex.getMessage().contains("The user does not have permission to edit."));

    }

    @Test
    void getAllItemByUserTest() {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, user, null);
        ItemDtoDate itemDtoDate = ItemMapper.toItemDtoDate(item);
        itemDtoDate.setComments(new ArrayList<>());

        assertEquals(itemDtoDate, itemService.getItemUser(item.getId(), user.getId()));
    }

    @Test
    void getItemEachUserById_Owner_WithNextBooking() {
        UserDto booker = new UserDto(0, "booker", "booker@gmail.com");
        booker = userService.addUser(booker);
        LocalDateTime start = LocalDateTime.parse("2100-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("2200-09-01T01:00");
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(start, end, itemDto.getId());
        BookingDto bookingDto = bookingService.addBooking(bookingDtoRequest, booker.getId());
        User owner = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, owner, null);
        ItemDtoDate itemDtoDate = ItemMapper.toItemDtoDate(item);
        itemDtoDate.setComments(new ArrayList<>());
        itemDtoDate.setNextBooking(new ItemDtoDate.ForItemBookingDto(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getBooker().getId()));

        assertEquals(itemDtoDate, itemService.getItemUser(itemDto.getId(), userDto.getId()));
    }

    @Test
    void getItemEachUserById_Owner_WithLastBooking() {
        UserDto booker = new UserDto(0,"booker", "booker@gmail.com");
        booker = userService.addUser(booker);

        LocalDateTime start = LocalDateTime.parse("1100-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("1200-09-01T01:00");

        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(start, end, itemDto.getId());
        BookingDto bookingDto = bookingService.addBooking(bookingDtoRequest, booker.getId());

        User owner = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, owner, null);

        ItemDtoDate itemDtoDate = ItemMapper.toItemDtoDate(item);
        itemDtoDate.setComments(new ArrayList<>());
        itemDtoDate.setLastBooking(new ItemDtoDate.ForItemBookingDto(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getBooker().getId()));

        assertEquals(itemDtoDate, itemService.getItemUser(itemDto.getId(), userDto.getId()));
    }

    @Test
    void getItemEachUserById_NotOwner() {
        UserDto userNotOwner = new UserDto(0,"notOwner", "notOwner@gmail.com");
        userNotOwner = userService.addUser(userNotOwner);
        User owner = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, owner, null);
        ItemDtoDate itemDtoDate = ItemMapper.toItemDtoDate(item);
        itemDtoDate.setComments(Collections.EMPTY_LIST);
        assertEquals(itemDtoDate, itemService.getItemUser(itemDto.getId(), userNotOwner.getId()));
    }

    @Test
    void getAllItemsOfOwner() {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, user, null);
        List<ItemDtoDate> itemDtoWithDateList = ItemMapper.toListItemDtoDate(List.of(item));

        assertEquals(itemDtoWithDateList, itemService.getAllItemByUser(userDto.getId(), 0, 2));
    }

    @Test
    void getItemsAvailableToRent_BlankList() {
        assertEquals(Collections.EMPTY_LIST,
                itemService.search("", 0, 2));
    }

    @Test
    void getItemsAvailableToRent() {
        assertEquals(List.of(itemDto),
                itemService.search("ite", 0, 2));
    }
}