package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.core.PrettyPrinter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String headerTitle = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(headerTitle) long userId) {
        return itemService.getAll(userId);
    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(headerTitle) long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto patch(@Valid @RequestBody ItemDto itemDto, @PathVariable long id, @RequestHeader(headerTitle) long userId) {
        return itemService.update(itemDto, id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        itemService.delete(id);
    }

    @GetMapping("/{id}")
    public ItemDto getId(@PathVariable long id) {
        return itemService.getById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemByParams(@RequestParam String text) {
        List<ItemDto> listItemDto = (text == null || StringUtils.isBlank(text) || StringUtils.isEmpty(text))
                ? List.of() : itemService.search(text);
        return listItemDto;
    }
}