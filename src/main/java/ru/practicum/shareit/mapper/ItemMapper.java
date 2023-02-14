package ru.practicum.shareit.mapper;

import org.springframework.lang.Nullable;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static Item toItem(ItemDto itemDto, @Nullable User user) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;
    }


    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public static List<ItemDto> toListItemDto(List<Item> items) {
        return items
                .stream()
                .map(i -> ItemMapper.toItemDto(i))
                .collect(Collectors.toList());
    }

    public static List<ItemDtoDate> toListItemDtoDate(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDtoDate)
                .collect(Collectors.toList());
    }

    public static ItemDtoDate toItemDtoDate(Item item) {
        ItemDtoDate itemDtoDate = new ItemDtoDate();
        itemDtoDate.setId(item.getId());
        itemDtoDate.setName(item.getName());
        itemDtoDate.setDescription(item.getDescription());
        itemDtoDate.setAvailable(item.getAvailable());
        return itemDtoDate;
    }
}
