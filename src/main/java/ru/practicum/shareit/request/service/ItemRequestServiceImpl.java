package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto addRequest(ItemRequestDto itemRequestDto, long requesterId) {
        User requester = fromOptionalUser(requesterId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(requester);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequestsForRequester(long requesterId) {
        User requester = fromOptionalUser(requesterId);
        List<ItemRequest> itemRequests = itemRequestRepository.findItemRequestsByRequesterOrderByCreatedDesc(requester);
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findAllByItemRequest(itemRequest);
            itemRequest.setItems(items);
        }
        return ItemRequestMapper.toItemRequestDtoList(itemRequests);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long requesterId, int from, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequesterIdIsNot(requesterId, PageRequest.of(from / size, size, sort)).getContent();
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findAllByItemRequest(itemRequest);
            itemRequest.setItems(items);
        }
        return ItemRequestMapper.toItemRequestDtoList(itemRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests() {

        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests = itemRequestRepository.findAll(sort);
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findAllByItemRequest(itemRequest);
            itemRequest.setItems(items);
        }
        return ItemRequestMapper.toItemRequestDtoList(itemRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getOneRequest(long requestId, long userId) {
        fromOptionalUser(userId);
        ItemRequest itemRequest = fromOptionalRequest(requestId);
        List<Item> items = itemRepository.findAllByItemRequest(itemRequest);
        itemRequest.setItems(items);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    private User fromOptionalUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));
    }

    private ItemRequest fromOptionalRequest(long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest not found."));
    }
}
