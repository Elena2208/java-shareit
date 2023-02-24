package ru.practicum.shareit.item;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentTest {
    @Autowired
    private JacksonTester<Comment> json;

    @Test
    void convert() throws IOException {
        User owner = new User(1, "owner", "owner@yandex.ru");
        Item item = new Item(1, "nameItem", "qqq", true, owner, null);
        User user = new User(1, "nameUser", "elena@yandex.ru");
        Comment comment = new Comment(1, "textComment",item,user,LocalDateTime.now());

        var result = json.write(comment);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.item.name");
        assertThat(result).hasJsonPath("$.author.name");
        assertThat(result).extractingJsonPathValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("textComment");
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("nameItem");
    }
}
