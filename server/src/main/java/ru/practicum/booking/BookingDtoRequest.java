package ru.practicum.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoRequest {

    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
