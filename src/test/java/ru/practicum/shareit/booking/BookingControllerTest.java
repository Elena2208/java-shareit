package ru.practicum.shareit.booking;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @MockBean
    private BookingService service;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;
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
    void addBooking() throws Exception {
        when(service.addBooking(bookingDtoRequest, 1L)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

    }

    @Test
    void approveBooking() throws Exception {
        bookingDto.setStatus(BookingStatus.APPROVED);
        when(service.approve(1L, true, 1L)).thenReturn(bookingDto);

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
        when(service.getBookingById(1L, 1L)).thenReturn(bookingDto);

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
        when(service.getBookingByUser(anyLong(), any(), anyInt(), anyInt())).thenReturn(bookings);

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
        when(service.getBookingByOwner(anyLong(), any(), anyInt(), anyInt())).thenReturn(bookings);

        mockMvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(bookings.size()));
    }
}

