package ru.practicum.shareit.itemrequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestTest {
    @Autowired
    private JacksonTester<ItemRequest> json;

    @Test
    void convert() throws IOException {
        User user = new User(1, "name", "elena@yandex.ru");
        ItemRequest itemRequest = new ItemRequest(1, "text", user,
                LocalDateTime.now(), Collections.emptyList());

        var result = json.write(itemRequest);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.requester");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).extractingJsonPathValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.requester.name").isEqualTo("name");
        assertThat(result).extractingJsonPathArrayValue("$.items").isInstanceOf(ArrayList.class);
    }
}
