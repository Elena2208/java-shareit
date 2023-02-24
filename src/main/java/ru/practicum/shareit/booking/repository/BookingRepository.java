package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long userId, LocalDateTime endDateTime,
                                                                                 LocalDateTime startDateTime,
                                                                                 Pageable pageable);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime endDateTime,
                                                                  Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId,
                                                                   LocalDateTime startDateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(Long userId,
                                                                              LocalDateTime startDateTime,
                                                                              BookingStatus bookingStatus,
                                                                              Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

    @Query("from Booking b where b.item.owner.id = :ownerId")
    List<Booking> findAllBookingsForOwner(@Param("ownerId") long ownerId, Pageable pageable);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.start <= :date and b.end >= :date")
    List<Booking> findBookingsCurrentForOwner(@Param("ownerId") long ownerId, @Param("date") LocalDateTime date,
                                              Pageable pageable);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.end < :date")
    List<Booking> findBookingsPastForOwner(@Param("ownerId") long ownerId, @Param("date") LocalDateTime date,
                                           Pageable pageable);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.start > :date")
    List<Booking> findBookingsFutureForOwner(@Param("ownerId") long ownerId, @Param("date") LocalDateTime date,
                                             Pageable pageable);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.status = :status")
    List<Booking> findBookingsByStatusForOwner(@Param("ownerId") long ownerId, @Param("status") BookingStatus status,
                                               Pageable pageable);

    @Query(value = "select * from bookings b " +
            "where b.item_id = :itemId and b.start_date < :date order by b.start_date limit  1",
            nativeQuery = true)
    Booking findBookingByItemWithDateBefore(@Param("itemId") long itemId, @Param("date") LocalDateTime date);

    @Query(value = "select * from bookings b " +
            "where b.item_id = :itemId and b.start_date > :date order by b.start_date limit  1",
            nativeQuery = true)
    Booking findBookingByItemWithDateAfter(@Param("itemId") long itemId, @Param("date") LocalDateTime date);
    @Query(value = "select exists(select * from bookings b " +
            "where b.booker_id = :userId and b.item_id = :itemId and  b.end_date < :date)",
            nativeQuery = true)
    boolean isExists(@Param("itemId") long itemId,
                     @Param("userId") long userId,
                     @Param("date") LocalDateTime date);
}

