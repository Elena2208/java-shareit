package ru.practicum.item;


import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto,
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
        return itemService.getItemUser(itemId, userId);

    }

    @GetMapping
    public List<ItemDtoDate> getItemOwnerUser(@RequestHeader(HEADER_USER_ID) long userId,
                                              @RequestParam(value = "from", required = false,
                                                      defaultValue = "0") int from,
                                              @RequestParam(
                                                      value = "size", required = false,
                                                      defaultValue = "20") int size) {
        return itemService.getAllItemByUser(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemAvailableToRenter(@RequestParam String text,
                                                  @RequestParam(value = "from", required = false,
                                                          defaultValue = "0") int from,
                                                  @RequestParam(value = "size", required = false,
                                                          defaultValue = "20") int size) {
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@RequestBody CommentDto commentDto,
                                       @PathVariable long itemId,
                                       @RequestHeader(HEADER_USER_ID) long userId) {
        return itemService.addComment(itemId, userId, commentDto);
    }
}