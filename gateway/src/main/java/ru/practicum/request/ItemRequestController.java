package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;



@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(HEADER_USER_ID) long requesterId,
                                             @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.addRequest(itemRequestDto, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object>  getAllRequestsForRequester(@RequestHeader(HEADER_USER_ID) long requesterId) {
        return itemRequestClient.getAllRequestsForRequester(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object>  getAllRequests(@PositiveOrZero @RequestParam(value = "from", required = false,
                                                defaultValue = "0") int from,
                                               @Positive @RequestParam(value = "size", required = false,
                                                defaultValue = "20") int size,
                                               @RequestHeader(HEADER_USER_ID) long requesterId) {
        return itemRequestClient.getAllRequests(requesterId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getOneRequest(@PathVariable long requestId,
                                        @RequestHeader(HEADER_USER_ID) long userId) {
        return itemRequestClient.getOneRequest(requestId, userId);
    }
}