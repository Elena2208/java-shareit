package ru.practicum.shareit.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                itemDto.getOwner(), itemDto.getRequest());
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getOwner(), item.getRequest());
    }

    public static List<ItemDto> toListItemDto(List<Item> items){
        return items
                .stream()
                .map(i -> ItemMapper.toItemDto(i))
                .collect(Collectors.toList());
    }
}
