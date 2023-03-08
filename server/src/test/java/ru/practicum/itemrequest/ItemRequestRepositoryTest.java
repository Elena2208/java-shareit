package ru.practicum.itemrequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.request.ItemRequest;
import ru.practicum.request.ItemRequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;


import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;

    private User requester1;
    private User requester2;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;

    @BeforeEach
    void saveRequests() {
        requester1 = new User(0,"One", "one@gmail.com");
        requester2 = new User(0,"Two", "two@gmail.com");

        itemRequest = new ItemRequest(0,"One", requester1, LocalDateTime.now(), null);
        itemRequest2 = new ItemRequest(0,"Two", requester2, LocalDateTime.now(), null);
    }

    @Test
    void findAllByRequester() {
        requester1 = userRepository.save(requester1);
        requester2 = userRepository.save(requester2);

        itemRequest = requestRepository.save(itemRequest);
        itemRequest2 = requestRepository.save(itemRequest2);

        List<ItemRequest> requests = requestRepository. findItemRequestsByRequesterOrderByCreatedDesc(requester1);

        assertThat(requests).hasSize(1).contains(itemRequest);

    }

    @Test
    void findAllByRequesterNotLike() {
        requester1 = userRepository.save(requester1);
        requester2 = userRepository.save(requester2);

        itemRequest = requestRepository.save(itemRequest);
        itemRequest2 = requestRepository.save(itemRequest2);
        List<ItemRequest> requests = requestRepository
                .findAllByRequesterIdIsNot(requester1.getId(), PageRequest.of(0, 2)).getContent();
        assertThat(requests).hasSize(1).contains(itemRequest2);
    }
}