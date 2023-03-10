package ru.practicum.item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, long userId);

    List<ItemDtoDate> getAllItemByUser(long userId, int from, int size);

    List<ItemDto> search(String text, int from, int size);

    ItemDtoDate getItemUser(long itemId, long userId);

    CommentDto addComment(long itemId, long userId, CommentDto commentDto);
}
