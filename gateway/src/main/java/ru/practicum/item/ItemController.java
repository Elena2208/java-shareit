package ru.practicum.item;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItem(@Valid @RequestBody ItemDto itemDto,
                                          @RequestHeader(HEADER_USER_ID) long userId) {
        return itemClient.addItem(itemDto,userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader(HEADER_USER_ID) long userId) {
        return itemClient.updateItem(itemId,userId,itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemEachUser(@PathVariable long itemId,
                                       @RequestHeader(HEADER_USER_ID) long userId) {
        return itemClient.getItemUser(itemId, userId);

    }

    @GetMapping
    public ResponseEntity<Object> getItemOwnerUser(@RequestHeader(HEADER_USER_ID) long userId,
                                              @PositiveOrZero @RequestParam(value = "from", required = false,
                                                      defaultValue = "0") int from,
                                              @Positive @RequestParam(
                                                      value = "size", required = false,
                                                      defaultValue = "20") int size) {
        return itemClient.getAllItemByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemAvailableToRenter(@RequestParam String text,
                                                  @PositiveOrZero @RequestParam(value = "from", required = false,
                                                          defaultValue = "0") int from,
                                                  @Positive @RequestParam(value = "size", required = false,
                                                          defaultValue = "20") int size) {
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(@Valid @RequestBody CommentDto commentDto,
                                       @PathVariable long itemId,
                                       @RequestHeader(HEADER_USER_ID) long userId) {
        return itemClient.addComment(itemId, userId, commentDto);
    }
}