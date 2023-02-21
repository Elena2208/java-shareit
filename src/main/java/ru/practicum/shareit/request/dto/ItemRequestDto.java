package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemRequestDto {
    private long id;
    @NotBlank
    private String description;
    private LocalDateTime created = LocalDateTime.now();
    private List<ItemDto> items = new ArrayList<>();
}
