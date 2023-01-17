package ru.practicum.shareit.item.storage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1;

    @Override
    public Item create(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public void delete(long id) {
        items.remove(id);
    }

    @Override
    public Item getById(long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new NotFoundException("Item not found.");
        }
        return item;
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> search(String text) {
        return items.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        StringUtils.containsIgnoreCase(item.getName(), text)
                                || StringUtils.containsIgnoreCase(item.getDescription(), text)
                )
                .collect(Collectors.toList());

    }
}
