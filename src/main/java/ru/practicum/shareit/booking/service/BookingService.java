package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingDtoRequest bookingDtoRequest, long userId);

    BookingDto approve(long bookingId, Boolean approved, long userId);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getBookingByUser(long bookerId, State state);

    List<BookingDto> getBookingByOwner(long ownerId, State state);
}
