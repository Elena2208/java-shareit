package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(ItemRequestDto itemRequestDto, long requesterId);

    List<ItemRequestDto> getAllRequestsForRequester(long requesterId);

    List<ItemRequestDto> getAllRequests(long requesterId, int from, int size);

    List<ItemRequestDto> getAllRequests();

    ItemRequestDto getOneRequest(long requestId, long userId);
}
