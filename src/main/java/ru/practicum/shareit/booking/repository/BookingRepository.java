package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
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
    List<Booking> findBookingByBookerIdOrderByStartDesc(long bookerId);

    @Query("from Booking b where b.booker.id = :bookerId and b.start <= :date and b.end >= :date")
    List<Booking> findBookingsCurrentForBooker(@Param("bookerId") long bookerId,
                                               @Param("date") LocalDateTime date, Sort sort);

    @Query("from Booking b where b.booker.id = :bookerId and b.end < :date")
    List<Booking> findBookingsPastForBooker(@Param("bookerId") long bookerId, @Param("date") LocalDateTime date,
                                            Sort sort);

    @Query("from Booking b where b.booker.id = :bookerId and b.start > :date")
    List<Booking> findBookingsFutureForBooker(@Param("bookerId") long bookerId, @Param("date") LocalDateTime date,
                                              Sort sort);

    @Query("from Booking b where b.booker.id = :bookerId and b.status = :status")
    List<Booking> findBookingsByStatusAndBookerId(@Param("bookerId") long bookerId, BookingStatus status);

    @Query("from Booking b where b.item.owner.id = :ownerId")
    List<Booking> findAllBookingsForOwner(@Param("ownerId") long ownerId, Sort sort);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.start <= :date and b.end >= :date")
    List<Booking> findBookingsCurrentForOwner(@Param("ownerId") long ownerId, @Param("date") LocalDateTime date,
                                              Sort sort);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.end < :date")
    List<Booking> findBookingsPastForOwner(@Param("ownerId") long ownerId, @Param("date") LocalDateTime date,
                                           Sort sort);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.start > :date")
    List<Booking> findBookingsFutureForOwner(@Param("ownerId") long ownerId, @Param("date") LocalDateTime date,
                                             Sort sort);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.status = :status")
    List<Booking> findBookingsByStatusForOwner(@Param("ownerId") long ownerId, @Param("status") BookingStatus status);


    Booking findBookingByItemAndBookerAndStatusIsAndEndIsBefore(Item item, User userId,
                                                                BookingStatus status, LocalDateTime now);

    @Query(value = "select * from bookings b " +
            "where b.item_id = :itemId and b.start_date < :date order by b.start_date limit  1",
            nativeQuery = true)
    Booking findBookingByItemWithDateBefore(@Param("itemId") long itemId, @Param("date") LocalDateTime date);

    @Query(value = "select * from bookings b " +
            "where b.item_id = :itemId and b.start_date > :date order by b.start_date limit  1",
            nativeQuery = true)
    Booking findBookingByItemWithDateAfter(@Param("itemId") long itemId, @Param("date") LocalDateTime date);

}