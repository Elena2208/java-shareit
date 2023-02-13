package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    private static final  String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingDtoRequest bookingDtoRequest,
                             @RequestHeader(HEADER_USER_ID) long userId) {
        return bookingService.addBooking(bookingDtoRequest, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto approve(@PathVariable long bookingId,
                              @RequestParam Boolean approved,
                              @RequestHeader(HEADER_USER_ID) long userId) {
        return bookingService.approve(bookingId, approved, userId);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId,
                                 @RequestHeader(HEADER_USER_ID) long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingByUser(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") State state,
            @RequestHeader(HEADER_USER_ID) long bookerId) {
        return bookingService.getBookingByUser(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForOwner(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") State state,
            @RequestHeader(HEADER_USER_ID) long ownerId) {
        return bookingService.getBookingByOwner(ownerId, state);
    }
}
