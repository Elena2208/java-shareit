package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingClient bookingClient;


    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public  ResponseEntity<Object> create(@Valid @RequestBody BookingDtoRequest bookingDtoRequest,
                             @RequestHeader(HEADER_USER_ID) long userId) {
        return bookingClient.addBooking(bookingDtoRequest, userId);
    }

    @PatchMapping("{bookingId}")
    public  ResponseEntity<Object> approve(@PathVariable long bookingId,
                              @RequestParam Boolean approved,
                              @RequestHeader(HEADER_USER_ID) long userId) {
        return bookingClient.approve(bookingId, approved, userId);
    }

    @GetMapping("{bookingId}")
    public  ResponseEntity<Object>getBooking(@PathVariable long bookingId,
                                 @RequestHeader(HEADER_USER_ID) long userId) {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public  ResponseEntity<Object> getBookingByUser(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") State state,
            @RequestHeader(HEADER_USER_ID) long bookerId,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        return bookingClient.getBookingByUser(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForOwner(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") State state,
            @RequestHeader(HEADER_USER_ID) long ownerId,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        return bookingClient.getBookingByOwner(ownerId, state, from, size);
    }
}
