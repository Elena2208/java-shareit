package ru.practicum.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.request.ItemRequest;
import ru.practicum.user.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner(Pageable pageable, User owner);

    @Query("select i from Item i " +
            "where (lower(i.name) like concat('%', lower(:text),'%') " +
            "or lower(i.description) like concat('%', lower(:text), '%')) " +
            "and i.available = true")
    List<Item> findByNameOrDescription(Pageable pageable, @Param("text") String text);

    List<Item> findAllByItemRequest(ItemRequest itemRequest);
}


