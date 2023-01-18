package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.mapper.ItemMapper;

import ru.practicum.shareit.user.storage.UserStorage;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto create(ItemDto itemDto, long userId) {
        userIdValidation(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        validateItem(item);
        return ItemMapper.toItemDto(itemStorage.create(item));
    }

    public ItemDto getById(long id) {
        if (itemStorage.getById(id) != null) {
            return ItemMapper.toItemDto(itemStorage.getById(id));
        } else {
            throw new NotFoundException("Item not found.");
        }
    }

    public List<ItemDto> getAll(long userId) {
        return ItemMapper.toListItemDto(itemStorage.getAll()
                .stream()
                .filter(i -> i.getOwner() == userId)
                .collect(Collectors.toList()));
    }

    private void contains(Item item) {
        if (itemStorage.getAll().stream().anyMatch(it -> it.getName().equals(item.getName())
                || it.getDescription().equals(item.getDescription()))) {
            throw new AlreadyExistsException("The item already exists.");
        }
    }

    private void userIdValidation(long userId) {
        if (userStorage.getId(userId) == null) {
            throw new NotFoundException("User not found.");
        }
    }


    private void validateItem(Item item) {
        if (item.getName().isEmpty() || item.getName().isBlank() || item.getName() == null) {
            throw new ValidationException("The field cannot be empty.");
        }
        if (item.getDescription() == null || item.getDescription().isEmpty() || item.getDescription().isBlank()) {
            throw new ValidationException("The field cannot be empty.");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("The field cannot be empty.");
        }
    }


    public ItemDto update(ItemDto itemDto, long id, long userId) {
        userIdValidation(userId);
        Item oldItem = itemStorage.getById(id);
        if (userId != oldItem.getOwner()) {
            throw new NotFoundException("This is not the owner of the item.");
        }
        Item newItem = ItemMapper.toItem(itemDto);
        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        return ItemMapper.toItemDto(itemStorage.update(oldItem));
    }

    @Override
    public void delete(long id) {
        itemStorage.delete(id);
    }

    @Override
    public List<ItemDto> search(String text) {
        return ItemMapper.toListItemDto(itemStorage.search(text));
    }
}
