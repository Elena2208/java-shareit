package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
class BookingServiceTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    private Item item;
    private User booker;
    private User owner;
    private User anotherUser;
    private BookingDto bookingDto;

    private BookingDtoRequest bookingDtoRequest;
    private final LocalDateTime start = LocalDateTime.parse("2100-09-01T01:00");
    private final LocalDateTime end = LocalDateTime.parse("2110-09-01T01:00");

    @BeforeEach
    void prepare() {
        owner = new User(0, "owner", "owner@gmail.com");
        booker = new User(0, "booker", "booker@gmail.com");
        anotherUser = new User(0, "another", "another@gmail.com");
        owner = UserMapper.toUser(userService.addUser(UserMapper.toUserDto(owner)));
        booker = UserMapper.toUser(userService.addUser(UserMapper.toUserDto(booker)));
        anotherUser = UserMapper.toUser(userService.addUser(UserMapper.toUserDto(anotherUser)));

        item = new Item(0, "item", "item description", true, owner, null);
        item = ItemMapper.toItem(itemService.addItem(ItemMapper.toItemDto(item), owner.getId()), owner, null);

        bookingDtoRequest = new BookingDtoRequest(start, end, item.getId());
        bookingDto = bookingService.addBooking(bookingDtoRequest, booker.getId());
    }

    @Test
    void addBooking() {
        assertThat(bookingDto.getId()).isNotZero();
        assertThat(bookingDto.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(bookingDto.getBooker().getName()).isEqualTo(booker.getName());
        assertThat(bookingDto.getStart()).isEqualTo(start);
        assertThat(bookingDto.getEnd()).isEqualTo(end);
        assertThat(bookingDto.getItem().getId()).isEqualTo(item.getId());
        assertThat(bookingDto.getItem().getName()).isEqualTo(item.getName());
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void addBooking_WrongDate() {
        LocalDateTime start = LocalDateTime.parse("2000-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("1000-09-01T01:00");
        bookingDtoRequest.setStart(start);
        bookingDtoRequest.setEnd(end);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingDtoRequest, booker.getId()));
        assertThat(ex.getMessage()).contains("End before start.");
    }

    @Test
    void addBooking_NotAvailable() {
        item.setAvailable(false);
        ItemDto addedItem = itemService.addItem(ItemMapper.toItemDto(item), anotherUser.getId());
        ValidationException ex = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingDtoRequest, booker.getId()));
        assertThat(ex.getMessage()).contains("The item is already booked.");
    }

    @Test
    void addBooking_BookerIsOwner() {
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(bookingDtoRequest, owner.getId()));
        assertThat(ex.getMessage()).contains("The owner cannot book.");
    }

    @Test
    void approveBooking() {
        bookingDto.setStatus(BookingStatus.APPROVED);
        assertEquals(bookingDto, bookingService.approve(bookingDto.getId(), true, owner.getId()));
    }

    @Test
    void approveBookingUserNotFound() {
        bookingDto.setStatus(BookingStatus.APPROVED);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () ->  bookingService.approve(bookingDto.getId(),true,-120L));
        assertThat(ex.getMessage()).contains("Used does not have the right.");
    }

    @Test
    void getBookingByIdIfOwnerOrBooker() {
        assertEquals(bookingDto, bookingService.getBookingById(bookingDto.getId(), owner.getId()));
    }

    @Test
    void getBookingByIdIfOwnerOrBooker_BookingNotFound() {
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(-1, owner.getId()));
        assertThat(ex.getMessage()).contains("Booking not found.");
    }

    @Test
    void getBookingByIdIfOwnerOrBooker_UserNotFound() {
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(bookingDto.getId(), anotherUser.getId()));
        assertThat(ex.getMessage()).contains("User not found.");
    }

    @Test
    void getBookingByUserSorted_UserNotFound() {
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingByUser(-1, State.ALL, 0, 2));
        assertThat(ex.getMessage()).contains("User not found.");
    }

    @Test
    void getBookingByUserSorted_AllState() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByUser(booker.getId(), State.ALL, 0, 2));
    }

    @Test
    void getBookingByUserSorted_CurrentState() {
        LocalDateTime start = LocalDateTime.parse("1000-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("2100-09-01T01:00");
        bookingDtoRequest = new BookingDtoRequest(start, end, item.getId());
        bookingDto = bookingService.addBooking(bookingDtoRequest, booker.getId());
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByUser(booker.getId(), State.CURRENT, 0, 2));
    }

    @Test
    void getBookingByUserSorted_PastState() {
        LocalDateTime start = LocalDateTime.parse("1000-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("1500-09-01T01:00");
        bookingDtoRequest = new BookingDtoRequest(start, end, item.getId());
        bookingDto = bookingService.addBooking(bookingDtoRequest, booker.getId());
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByUser(booker.getId(), State.PAST, 0, 2));
    }

    @Test
    void getBookingByUserSorted_FutureState() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByUser(booker.getId(), State.FUTURE, 0, 2));
    }

    @Test
    void getBookingByUserSorted_WaitingStatus() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByUser(booker.getId(), State.WAITING, 0, 2));
    }

    @Test
    void getBookingByUserSorted_RejectedStatus() {
        bookingDto = bookingService.approve(bookingDto.getId(), false, owner.getId());
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByUser(booker.getId(), State.REJECTED, 0, 2));
    }

    @Test
    void getBookingByUserSorted_UnsupportedState() {
        UnknownStateException ex = assertThrows(UnknownStateException.class,
                () -> bookingService.getBookingByUser(booker.getId(),
                        State.UNSUPPORTED_STATUS, 0, 2));
        assertThat(ex.getMessage()).contains("Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    void getBookingByItemOwner_OwnerNotFound() {
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingByOwner(-1, State.ALL, 0, 2));
        assertThat(ex.getMessage()).contains("User not found");
    }

    @Test
    void getBookingByItemOwner_AllState() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByOwner(owner.getId(), State.ALL, 0, 2));
    }

    @Test
    void getBookingByItemOwner_CurrentState() {
        LocalDateTime start = LocalDateTime.parse("1000-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("2100-09-01T01:00");
        bookingDtoRequest = new BookingDtoRequest(start, end, item.getId());
        bookingDto = bookingService.addBooking(bookingDtoRequest, booker.getId());
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByOwner(owner.getId(), State.CURRENT, 0, 2));
    }

    @Test
    void getBookingByItemOwner_PastState() {
        LocalDateTime start = LocalDateTime.parse("1000-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("1500-09-01T01:00");
        bookingDtoRequest = new BookingDtoRequest(start, end, item.getId());
        bookingDto = bookingService.addBooking(bookingDtoRequest, booker.getId());
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByOwner(owner.getId(), State.PAST, 0, 2));
    }

    @Test
    void getBookingByItemOwner_FutureState() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByOwner(owner.getId(), State.FUTURE, 0, 2));
    }

    @Test
    void getBookingByItemOwner_WaitingStatus() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByOwner(owner.getId(), State.WAITING, 0, 2));
    }

    @Test
    void getBookingByItemOwner_RejectedStatus() {
        bookingDto = bookingService.approve(bookingDto.getId(), false, owner.getId());
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByOwner(owner.getId(), State.REJECTED, 0, 2));
    }

    @Test
    void getBookingByItemOwner_UnsupportedState() {
        UnknownStateException ex = assertThrows(UnknownStateException.class,
                () -> bookingService.getBookingByOwner(owner.getId(),
                        State.UNSUPPORTED_STATUS, 0, 2));
        assertThat(ex.getMessage()).contains("Unknown state: UNSUPPORTED_STATUS");
    }
}