package ru.practicum.shareit.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoDate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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

    public static BookingDto toBookingDto(Booking booking) {
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
