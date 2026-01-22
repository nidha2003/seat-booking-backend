package com.example.seatbooking.scheduler;

import com.example.seatbooking.service.SeatService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SeatCleanupScheduler {

    private final SeatService seatService;

    public SeatCleanupScheduler(SeatService seatService) {
        this.seatService = seatService;
    }

    /**
     * Runs every 1 minute
     * Cleans expired held seats
     */
    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredHolds() {
        seatService.releaseExpiredHolds();
    }
}