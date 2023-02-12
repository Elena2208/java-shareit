package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.mapper.CommentMapper;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, long userId) {
        User owner = fromOptionalUser(userId);
        Item item = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long itemId, long userId) {
        if (isOwner(itemId, userId)) {
            User user = fromOptionalUser(userId);
            Item oldItem = fromOptionalItem(itemId);
            Item update = ItemMapper.toItem(itemDto, user);
            Optional.ofNullable(update.getName()).ifPresent(oldItem::setName);
            Optional.ofNullable(update.getDescription()).ifPresent(oldItem::setDescription);
            Optional.ofNullable(update.getAvailable()).ifPresent(oldItem::setAvailable);
            return ItemMapper.toItemDto(itemRepository.save(oldItem));
        } else {
            throw new NoAccessException("The user does not have permission to edit.");
        }
    }

    @Override
    public List<ItemDtoDate> getAllItemByUser(long userId) {
        User owner = fromOptionalUser(userId);
        List<ItemDtoDate> items = ItemMapper.toListItemDtoDate(itemRepository.findByOwner(owner));
        for (ItemDtoDate item : items) {
            addBookingForItem(item);
        }
        items.sort(Comparator.comparingLong(ItemDtoDate::getId));
        return items;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (StringUtils.isBlank(text)) {
            return List.of();
        }
        return ItemMapper.toListItemDto(itemRepository.findByNameOrDescription(text));
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
        LocalDateTime now = LocalDateTime.now();
        Booking booking = bookingRepository.findBookingByItemAndBookerAndStatusIsAndEndIsBefore(item, author, BookingStatus.APPROVED, now);
        if (booking == null) {
            throw new ValidationException("The item has not been booked yet.");
        }
        Comment comment = CommentMapper.toComment(commentDto, item, author);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
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
}
