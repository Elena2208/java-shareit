package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDate;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, long userId);

    List<ItemDtoDate> getAllItemByUser(long userId);

    List<ItemDto> search(String text);

    ItemDtoDate getItemUser(long itemId, long userId);

    CommentDto addComment(long itemId, long userId, CommentDto commentDto);
}
