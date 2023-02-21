package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDto addBooking(BookingDtoRequest bookingDtoRequest, long userId) {
        User user = getUser(userId);
        Item item = getItem(bookingDtoRequest.getItemId());
        if (BooleanUtils.isFalse(item.getAvailable())) {
            throw new ValidationException("The item is already booked.");
        }
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("The owner cannot book.");
        }
        if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            throw new ValidationException("End before start.");
        }
        Booking booking = BookingMapper.toBooking(bookingDtoRequest, user, item);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approve(long bookingId, Boolean approved, long userId) {
        Booking booking = getBooking(bookingId);
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("The item is already booked.");
        }
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Used does not have the right.");
        }
        booking.setStatus(Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(long bookingId, long userId) {
        Booking booking = getBooking(bookingId);
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("User not found.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingByUser(long bookerId, State state, int from, int size) {
        getUser(bookerId);
        List<Booking> bookings;
        Pageable pageable = PageRequest.of(from / size, size);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository
                        .findAllByBookerIdOrderByStartDesc(bookerId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findAllByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(bookerId, now, now, pageable);
                break;
            case PAST:
                bookings = bookingRepository
                        .findAllByBookerIdAndEndIsBeforeOrderByStartDesc(bookerId, now, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findAllByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, now, pageable);
                break;
            case WAITING:
                bookings = bookingRepository
                        .findAllByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(
                                bookerId, now, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository
                        .findAllByBookerIdAndStatusIsOrderByStartDesc(bookerId, BookingStatus.REJECTED,pageable);
                break;
            default:
                throw new UnknownStateException("Unknown state: " + state);
        }
        return BookingMapper.toListBookingDto(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingByOwner(long ownerId, State state, int from, int size) {
        getUser(ownerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable=PageRequest.of(from / size, size, sort);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllBookingsForOwner(ownerId,pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsCurrentForOwner(ownerId, LocalDateTime.now(),pageable);
                break;
            case PAST:
                bookings = bookingRepository.findBookingsPastForOwner(ownerId, LocalDateTime.now(),pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsFutureForOwner(ownerId, LocalDateTime.now(),pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByStatusForOwner(ownerId, BookingStatus.WAITING,pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByStatusForOwner(ownerId, BookingStatus.REJECTED,pageable);
                break;
            default:
                throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.toListBookingDto(bookings);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found."));
    }

    private Booking getBooking(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found."));
    }
}
