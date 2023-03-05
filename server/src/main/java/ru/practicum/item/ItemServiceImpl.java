package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingRepository;
import ru.practicum.exception.NoAccessException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.BookingMapper;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.mapper.ItemMapper;
import ru.practicum.request.ItemRequest;
import ru.practicum.request.ItemRequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, long userId) {
        User owner = fromOptionalUser(userId);
        ItemRequest itemRequest = itemDto.getRequestId() != null ? fromOptionalToRequest(itemDto.getRequestId()) : null;
        Item item = ItemMapper.toItem(itemDto, owner, itemRequest);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long itemId, long userId) {
        if (isOwner(itemId, userId)) {
            User user = fromOptionalUser(userId);
            Item oldItem = fromOptionalItem(itemId);
            Item update = ItemMapper.toItem(itemDto, user, null);
            Optional.ofNullable(update.getName()).ifPresent(oldItem::setName);
            Optional.ofNullable(update.getDescription()).ifPresent(oldItem::setDescription);
            Optional.ofNullable(update.getAvailable()).ifPresent(oldItem::setAvailable);
            return ItemMapper.toItemDto(itemRepository.save(oldItem));
        } else {
            throw new NoAccessException("The user does not have permission to edit.");
        }
    }

    @Override
    public List<ItemDtoDate> getAllItemByUser(long userId, int from, int size) {
        User owner = fromOptionalUser(userId);
        List<ItemDtoDate> items = ItemMapper.toListItemDtoDate(itemRepository
                .findByOwner(PageRequest.of(from / size, size, Sort.by("id")), owner));
        for (ItemDtoDate item : items) {
            addBookingForItem(item);
        }
        return items;
    }

    @Override
    public List<ItemDto> search(String text, int from, int size) {
        if (StringUtils.isBlank(text)) {
            return List.of();
        }
        List<Item> items = itemRepository.findByNameOrDescription(PageRequest.of(from / size, size), text);
        return items.stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoDate getItemUser(long itemId, long userId) {
        fromOptionalUser(userId);
        Item item = fromOptionalItem(itemId);
        ItemDtoDate itemDtoDate = ItemMapper.toItemDtoDate(item);
        addComments(List.of(itemDtoDate));
        if (item.getOwner().getId() != userId) {
            return itemDtoDate;
        } else {
            return addBookingForItem(itemDtoDate);
        }
    }

    private ItemDtoDate addBookingForItem(ItemDtoDate itemDtoDate) {
        Booking lastBooking = bookingRepository.findBookingByItemWithDateBefore(itemDtoDate.getId(), LocalDateTime.now());
        Booking newtBooking = bookingRepository.findBookingByItemWithDateAfter(itemDtoDate.getId(), LocalDateTime.now());
        if (lastBooking != null) {
            itemDtoDate.setLastBooking(BookingMapper.toItemBookingDto(lastBooking));
        } else {
            itemDtoDate.setLastBooking(null);
        }
        if (newtBooking != null) {
            itemDtoDate.setNextBooking(BookingMapper.toItemBookingDto(newtBooking));
        } else {
            itemDtoDate.setNextBooking(null);
        }
        return itemDtoDate;
    }

    private void addComments(List<ItemDtoDate> itemDtoDate) {
        List<Comment> comments;
        for (ItemDtoDate item : itemDtoDate) {
            comments = commentRepository.findByItemId(item.getId());
            item.setComments((CommentMapper.toListCommentsDto(comments)));
        }
    }

    @Override
    public CommentDto addComment(long itemId, long userId, CommentDto commentDto) {
        Item item = fromOptionalItem(itemId);
        User author = fromOptionalUser(userId);

        if (bookingRepository.isExists(itemId, userId, LocalDateTime.now())) {

            Comment comment = CommentMapper.toComment(commentDto, item, author);
            commentDto = CommentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new ValidationException("User did not book item.");
        }
        return commentDto;
    }

    private User fromOptionalUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
    }

    private Item fromOptionalItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found."));
    }

    private boolean isOwner(long itemId, long userId) {
        return fromOptionalItem(itemId).getOwner().getId() == userId;
    }

    private ItemRequest fromOptionalToRequest(long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Request not found."));
    }

}
