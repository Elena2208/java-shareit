package ru.practicum.mapper;

import org.jetbrains.annotations.NotNull;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingDto;
import ru.practicum.booking.BookingDtoRequest;
import ru.practicum.item.Item;
import ru.practicum.item.ItemDtoDate;
import ru.practicum.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static Booking toBooking(BookingDtoRequest bookingDtoRequest, User booker, Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoRequest.getStart());
        booking.setEnd(bookingDtoRequest.getEnd());
        booking.setBooker(booker);
        booking.setItem(item);
        return booking;
    }

    public static @NotNull BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setItem(new BookingDto.ItemBooking(booking.getItem().getId(), booking.getItem().getName()));
        bookingDto.setBooker(new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()));
        return bookingDto;
    }

    public static List<BookingDto> toListBookingDto(List<Booking> list) {
        return list.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public static ItemDtoDate.ForItemBookingDto toItemBookingDto(Booking booking) {
        return new ItemDtoDate.ForItemBookingDto(booking.getId(), booking.getStart(),
                                                 booking.getEnd(), booking.getBooker().getId());
    }
}
