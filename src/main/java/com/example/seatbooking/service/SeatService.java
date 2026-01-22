package com.example.seatbooking.service;

import com.example.seatbooking.entity.SeatStatus;
import com.example.seatbooking.entity.Seat;
import com.example.seatbooking.repository.SeatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    // ------- READ AVAILABLE SEATS -------
    public List<Seat> getAvailableSeats(UUID showId) {
        return seatRepository.findByMovieShowId(showId)
                .stream()
                .filter(s -> s.getStatus() == SeatStatus.AVAILABLE)
                .toList();
    }

    // ------- HOLD SEAT WITH PESSIMISTIC LOCK -------
    @Transactional
    public Seat holdSeat(UUID seatId) {

        // Locks the row for write (no parallel hold on same seat)
        Seat seat = seatRepository.findByIdForUpdate(seatId);

        // Check if seat expired before hold
        if (seat.getStatus() == SeatStatus.HELD && seat.getHeldUntil() != null) {
            if (seat.getHeldUntil().isBefore(LocalDateTime.now())) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setHeldUntil(null);
            }
        }

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new RuntimeException("Seat not available for hold");
        }

        // Hold for 5 minutes
        seat.setStatus(SeatStatus.HELD);
        seat.setHeldUntil(LocalDateTime.now().plusMinutes(5));

        return seatRepository.save(seat);
    }

    // ------- BOOK SEAT WITH LOCK -------
    @Transactional
    public Seat bookSeat(UUID seatId) {

        // Lock again to avoid double booking
        Seat seat = seatRepository.findByIdForUpdate(seatId);

        // Check expiration again
        if (seat.getStatus() == SeatStatus.HELD && seat.getHeldUntil() != null) {
            if (seat.getHeldUntil().isBefore(LocalDateTime.now())) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setHeldUntil(null);
                seatRepository.save(seat);
                throw new RuntimeException("Hold expired, please retry booking");
            }
        }

        if (seat.getStatus() != SeatStatus.HELD) {
            throw new RuntimeException("Seat must be held before booking");
        }

        // Mark as booked
        seat.setStatus(SeatStatus.BOOKED);
        seat.setHeldUntil(null);

        return seatRepository.save(seat);
    }

    // ------- CLEANUP EXPIRED HOLDS (called by scheduler) -------
    @Transactional
    public void releaseExpiredHolds() {
        List<Seat> seats = seatRepository.findAll();

        seats.stream()
                .filter(s -> s.getStatus() == SeatStatus.HELD)
                .filter(s -> s.getHeldUntil() != null && s.getHeldUntil().isBefore(LocalDateTime.now()))
                .forEach(s -> {
                    s.setStatus(SeatStatus.AVAILABLE);
                    s.setHeldUntil(null);
                    seatRepository.save(s);
                });
    }
}
