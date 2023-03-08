package ru.practicum.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemClient itemClient;
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
        ResponseEntity<Object> response = ResponseEntity.status(201).body(itemDto);
        when(itemClient.addItem(any(), anyLong())).thenReturn(response);
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
        ResponseEntity<Object> response = ResponseEntity.status(201).body(itemDto);
        when(itemClient.updateItem(anyLong(),anyLong(),any())).thenReturn(response);
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
        ResponseEntity<Object> response = ResponseEntity.status(201).body(itemDtoDate);
        when(itemClient.getItemUser(anyLong(), anyLong())).thenReturn(response);
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
        ResponseEntity<Object> response = ResponseEntity.status(201).body(items);
        when(itemClient.getAllItemByUser(anyLong(), anyInt(), anyInt())).thenReturn(response);
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
        ResponseEntity<Object> response = ResponseEntity.status(201).body(items);
        when(itemClient.search(anyString(), anyInt(), anyInt())).thenReturn(response);

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
        ResponseEntity<Object> response = ResponseEntity.status(201).body(commentDto);
        when(itemClient.addComment(1L, 1L, commentDto)).thenReturn(response);
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