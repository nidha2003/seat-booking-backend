package com.example.seatbooking.controller;

import com.example.seatbooking.entity.Seat;
import com.example.seatbooking.service.SeatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/available/{showId}")
    public List<Seat> available(@PathVariable UUID showId) {
        return seatService.getAvailableSeats(showId);
    }

    @PostMapping("/hold/{seatId}")
    public Seat hold(@PathVariable UUID seatId) {
        return seatService.holdSeat(seatId);
    }

    @PostMapping("/book/{seatId}")
    public Seat book(@PathVariable UUID seatId) {
        return seatService.bookSeat(seatId);
    }
}
