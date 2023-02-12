package ru.practicum.shareit.item.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDate;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto,
                           @RequestHeader(HEADER_USER_ID) long userId) {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader(HEADER_USER_ID) long userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoDate getItemEachUser(@PathVariable long itemId,
                                       @RequestHeader(HEADER_USER_ID) long userId) {
        return itemService.getItemUser(itemId,userId);

    }

    @GetMapping
    public List<ItemDtoDate> getItemOwnerUser(@RequestHeader(HEADER_USER_ID) long userId) {
     return    itemService.getAllItemByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemAvailableToRenter(@RequestParam String text) {
        return itemService.search(text);
    }

    //пошли комменты
    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@Valid @RequestBody CommentDto commentDto,
                                       @PathVariable long itemId,
                                       @RequestHeader(HEADER_USER_ID) long userId) {
        return itemService.addComment(itemId, userId, commentDto);
    }
}