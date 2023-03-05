package ru.practicum.mapper;

import org.springframework.lang.Nullable;
import ru.practicum.item.Item;
import ru.practicum.item.ItemDto;
import ru.practicum.item.ItemDtoDate;
import ru.practicum.request.ItemRequest;
import ru.practicum.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static Item toItem(ItemDto itemDto, @Nullable User user, @Nullable ItemRequest request) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        item.setItemRequest(request);
        return item;
    }


    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null);
        return itemDto;
    }

    public static List<ItemDto> toListItemDto(List<Item> items) {
        return items
                .stream()
                .map(ItemMapper::toItemDto)
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
        itemDtoDate.setRequestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null);
        return itemDtoDate;
    }
}
