package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
    public List<BookingDto> getBookingByUser(long bookerId, State state) {
        getUser(bookerId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return BookingMapper.toListBookingDto(bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId));
            case CURRENT:
                return BookingMapper.toListBookingDto(bookingRepository
                        .findAllByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(bookerId, now, now));
            case PAST:
                return BookingMapper.toListBookingDto(bookingRepository
                        .findAllByBookerIdAndEndIsBeforeOrderByStartDesc(bookerId, now));
            case FUTURE:
                return BookingMapper.toListBookingDto(bookingRepository
                        .findAllByBookerIdAndStartIsAfterOrderByStartDesc(bookerId,now));
            case WAITING:
                return BookingMapper.toListBookingDto(bookingRepository
                        .findAllByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(
                                bookerId,now,BookingStatus.WAITING));
            case REJECTED:
                return BookingMapper.toListBookingDto(bookingRepository
                        .findAllByBookerIdAndStatusIsOrderByStartDesc(bookerId, BookingStatus.REJECTED));
            default: throw new UnknownStateException("Unknown state: " + state);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingByOwner(long ownerId, State state) {
        getUser(ownerId);
        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllBookingsForOwner(ownerId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsCurrentForOwner(ownerId, LocalDateTime.now(), sort);
                break;
            case PAST:
                bookings = bookingRepository.findBookingsPastForOwner(ownerId, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsFutureForOwner(ownerId, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByStatusForOwner(ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByStatusForOwner(ownerId, BookingStatus.REJECTED);
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
