package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(HEADER_USER_ID) long requesterId,
                                     @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addRequest(itemRequestDto, requesterId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsForRequester(@RequestHeader(HEADER_USER_ID) long requesterId) {
        return itemRequestService.getAllRequestsForRequester(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestParam(value = "from", required = false,
            defaultValue = "0") int from,
                                               @RequestParam(value = "size", required = false,
                                                       defaultValue = "20") int size,
                                               @RequestHeader(HEADER_USER_ID) long requesterId) {
        return itemRequestService.getAllRequests(requesterId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getOneRequest(@PathVariable long requestId,
                                        @RequestHeader(HEADER_USER_ID) long userId) {
        return itemRequestService.getOneRequest(requestId, userId);
    }
}