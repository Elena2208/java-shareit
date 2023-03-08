package ru.practicum.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private Item item;
    private User user;

    @BeforeEach
    void  beforeEach() {
        user = new User(0, "One", "one@gmail.com");
        item = new Item(0, "item", "item 1", true, user, null);
    }

    @Test
    void findByOwner() {
        user = userRepository.save(user);
        item = itemRepository.save(item);

        List<Item> items = itemRepository.findByOwner(PageRequest.of(0, 2), user);
        assertThat(items).hasSize(1).contains(item);
    }

    @Test
    void findByNameOrDescription() {
        user = userRepository.save(user);
        item = itemRepository.save(item);

        List<Item> items = itemRepository.findByNameOrDescription(PageRequest.of(0, 2), "item");
        assertThat(items).hasSize(1).contains(item);
    }
}
