package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item);

    void delete(long id);

    Item getById(long id);

    List<Item> getAll();

    List<Item> search(String text);
}
