package ru.practicum.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingClient bookingClient;
    private BookingDto bookingDto;
    private BookingDtoRequest bookingDtoRequest;
    private final LocalDateTime start = LocalDateTime.parse("2100-09-01T01:00");
    private final LocalDateTime end = LocalDateTime.parse("2110-09-01T01:00");

    @BeforeEach
    void create() {
        bookingDto = new BookingDto(1L, start, end, null, null, BookingStatus.WAITING);
        bookingDtoRequest = new BookingDtoRequest(start, end, 1L);
    }

    @Test
    void createBookingCorrect() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(201).body(bookingDto);
        when(bookingClient.addBooking(bookingDtoRequest, 1L)).thenReturn(response);
        Long userId = 1L;
        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }


    @Test
    void createBookingStartAfterEnd() throws Exception {
        bookingDto.setStart(bookingDto.getStart().plusDays(100));

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }


    @Test
    void approveBooking() throws Exception {
        bookingDto.setStatus(BookingStatus.APPROVED);
        ResponseEntity<Object> response = ResponseEntity.status(201).body(bookingDto);
        when(bookingClient.approve(1L, true, 1L)).thenReturn(response);
        mockMvc.perform(
                        patch("/bookings/{bookingId}", 1L)
                                .param("approved", "true")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.start").value("2100-09-01T01:00:00"))
                .andExpect(jsonPath("$.end").value("2110-09-01T01:00:00"))
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.toString()));
    }

    @Test
    void getBooking() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(201).body(bookingDto);
        when(bookingClient.getBookingById(1L, 1L)).thenReturn(response);
        mockMvc.perform(
                        get("/bookings/{bookingId}", 1L)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.start").value("2100-09-01T01:00:00"))
                .andExpect(jsonPath("$.end").value("2110-09-01T01:00:00"))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.toString()));
    }

    @Test
    void getBookingByUserSorted() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);
        ResponseEntity<Object> response = ResponseEntity.status(201).body(bookings);
        when(bookingClient.getBookingByUser(anyLong(), any(), anyInt(), anyInt())).thenReturn(response);
        mockMvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(bookings.size()));
    }


    @Test
    void getBookingsForItemOwner() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);
        ResponseEntity<Object> response = ResponseEntity.status(201).body(bookings);
        when(bookingClient.getBookingByOwner(anyLong(), any(), anyInt(), anyInt())).thenReturn(response);
        mockMvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(bookings.size()));
    }
}