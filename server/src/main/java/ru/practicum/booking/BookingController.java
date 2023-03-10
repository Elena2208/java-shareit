package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestBody BookingDtoRequest bookingDtoRequest,
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
            @RequestHeader(HEADER_USER_ID) long bookerId,
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        return bookingService.getBookingByUser(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForOwner(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") State state,
            @RequestHeader(HEADER_USER_ID) long ownerId,
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        return bookingService.getBookingByOwner(ownerId, state, from, size);
    }
}
