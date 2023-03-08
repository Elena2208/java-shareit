package ru.practicum.booking;



import ru.practicum.State;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingDtoRequest bookingDtoRequest, long userId);

    BookingDto approve(long bookingId, Boolean approved, long userId);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getBookingByUser(long bookerId, State state, int from, int size);

    List<BookingDto> getBookingByOwner(long ownerId, State state, int from, int size);
}
