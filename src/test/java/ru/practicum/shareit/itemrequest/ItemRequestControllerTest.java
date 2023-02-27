package ru.practicum.shareit.itemrequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService requestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    ItemRequestDto itemRequest1;
    ItemRequestDto itemRequest2;

    @BeforeEach
    void createRequests() {
        itemRequest1 = new ItemRequestDto(1L, "First request", LocalDateTime.now(), null);
        itemRequest2 = new ItemRequestDto(2L, "First request", LocalDateTime.now(), null);
    }

    @Test
    void addRequest() throws Exception {
        when(requestService.addRequest(any(), anyLong())).thenReturn(itemRequest1);

        mockMvc.perform(
                        post("/requests")
                                .content(mapper.writeValueAsString(itemRequest1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description").value("First request"));

    }

    @Test
    void getAllRequestsForRequester() throws Exception {
        List<ItemRequestDto> requests = List.of(itemRequest1);
        when(requestService.getAllRequestsForRequester(anyLong())).thenReturn(requests);

        mockMvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(requests.size()));
    }

    @Test
    void getAllRequests() throws Exception {
        List<ItemRequestDto> requests = List.of(itemRequest1, itemRequest2);
        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(requests);

        mockMvc.perform(
                        get("/requests/all")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(requests.size()));
    }

    @Test
    void getOneRequest() throws Exception {
        when(requestService.getOneRequest(anyLong(), anyLong())).thenReturn(itemRequest1);

        mockMvc.perform(
                        get("/requests/{requestId}", 1L)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description").value("First request"));
    }
}