package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.booking.BookingDto;
import ru.practicum.booking.BookingDtoRequest;
import ru.practicum.booking.BookingRepository;
import ru.practicum.booking.BookingService;
import ru.practicum.exception.NoAccessException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.ItemMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.user.User;
import ru.practicum.user.UserDto;
import ru.practicum.user.UserService;

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
    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void prepare() {
        userDto = new UserDto(0, "user", "user@gmail.com");
        userDto = userService.addUser(userDto);
        itemDto = new ItemDto(0, "item1", "item description", true, null);
        itemDto = itemService.addItem(itemDto, userDto.getId());

    }

    @Test
    void addItem() {
        assertThat(itemDto.getName()).isEqualTo("item1");
        assertThat(itemDto.getDescription()).isEqualTo("item description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isNull();
    }

    @Test
    void updateItem_WithName() {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setName("updated name");
        itemDto.setName("updated name");

        assertEquals(itemDto, itemService.updateItem(updatedItem, itemDto.getId(), userDto.getId()));
    }

    @Test
    void updateItem_WithDescription() {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setDescription("updated description");
        itemDto.setDescription("updated description");

        assertEquals(itemDto, itemService.updateItem(updatedItem, itemDto.getId(), userDto.getId()));
    }

    @Test
    void updateItem_WithAvailable() {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setAvailable(false);
        itemDto.setAvailable(false);

        assertEquals(itemDto, itemService.updateItem(updatedItem, itemDto.getId(), userDto.getId()));
    }

    @Test
    void updateItem_UserNotOwner() {
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
    void getItemEachUserById_Owner() {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, user, null);
        ItemDtoDate itemDtoWithDate = ItemMapper.toItemDtoDate(item);
        itemDtoWithDate.setComments(new ArrayList<>());

        assertEquals(itemDtoWithDate, itemService.getItemUser(itemDto.getId(), userDto.getId()));
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
        UserDto booker = new UserDto(0, "booker", "booker@gmail.com");
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
        UserDto userNotOwner = new UserDto(0, "notOwner", "notOwner@gmail.com");
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
        assertEquals(Collections.EMPTY_LIST, itemService.search("", 0, 2));
    }

    @Test
    void getItemsAvailableToRent() {
        assertEquals(List.of(itemDto),
                itemService.search("ite", 0, 2));
    }

    @Test
    void addCommentToItem() {
        UserDto author = new UserDto(0,"author", "author@gmail.com");
        author = userService.addUser(author);
        CommentDto commentDto = new CommentDto(0,"comment", author.getName(), null);
        LocalDateTime start = LocalDateTime.parse("1100-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("1200-09-01T01:00");
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(start, end, itemDto.getId());
        bookingService.addBooking(bookingDtoRequest, author.getId());
        CommentDto result = itemService.addComment(itemDto.getId(), author.getId(), commentDto);
        assertThat(result.getText()).isEqualTo("comment");
        assertThat(result.getAuthorName()).isEqualTo(author.getName());
    }


    @Test
    void addCommentToItem_UserDidntBook() {
        CommentDto commentDto = new CommentDto(0, "comment", "author", null);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.addComment(itemDto.getId(), userDto.getId(), commentDto));
        assertThat(ex.getMessage()).contains("User did not book item.");
    }
}