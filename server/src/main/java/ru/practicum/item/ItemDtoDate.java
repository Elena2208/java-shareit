package ru.practicum.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoDate {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private ForItemBookingDto lastBooking;
    private ForItemBookingDto nextBooking;
    private List<CommentDto> comments;

    @Data
    public static class ForItemBookingDto {
        private final Long id;
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final Long bookerId;
    }
}
