package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDate;
import ru.practicum.shareit.item.service.ItemService;


import java.time.LocalDateTime;
import java.util.List;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private ItemDto itemDto;
    private ItemDtoDate itemDtoDate;
    private CommentDto commentDto;

    @BeforeEach
    void createItem() {
        itemDto = new ItemDto(1L, "itemDto1", "one", true, 1L);
        itemDtoDate = new ItemDtoDate(1L, "dto", "dto with date", true,
                1L, null, null, null);
        commentDto = new CommentDto(1L, "comment", "Elena", LocalDateTime.now());
    }

    @Test
    void addItem() throws Exception {
        when(itemService.addItem(any(), anyLong())).thenReturn(itemDto);
        mockMvc.perform(
                        post("/items")
                                .content(mapper.writeValueAsString(itemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(any(), anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(
                        patch("/items/{itemId}", itemDto.getId())
                                .content(mapper.writeValueAsString(itemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    void getItemEachUser() throws Exception {
        when(itemService.getItemUser(anyLong(), anyLong())).thenReturn(itemDtoDate);
        mockMvc.perform(
                        get("/items/{itemId}", 1L)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(itemDtoDate.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoDate.getDescription()));
    }

    @Test
    void getItemOwnerUser() throws Exception {
        List<ItemDtoDate> items = List.of(itemDtoDate);
        when(itemService.getAllItemByUser(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemDtoDate));
        mockMvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(items.size()));
    }

    @Test
    void getItemAvailableToRenter() throws Exception {
        List<ItemDto> items = List.of(itemDto);
        when(itemService.search(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mockMvc.perform(
                        get("/items/search")
                                .param("text", "one")
                                .param("from", "0")
                                .param("size", "3")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(items.size()));
    }

    @Test
    void addCommentToItem() throws Exception {
        when(itemService.addComment(1L, 1L, commentDto)).thenReturn(commentDto);

        mockMvc.perform(
                        post("/items/{itemId}/comment", 1L)
                                .content(mapper.writeValueAsString(commentDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.text").value("comment"))
                .andExpect(jsonPath("$.authorName").value("Elena"));
    }
}
