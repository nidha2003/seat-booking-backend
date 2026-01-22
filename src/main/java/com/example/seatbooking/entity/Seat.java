package com.example.seatbooking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Seat {

    @Id
    @GeneratedValue
    private UUID id;

    private int seatNumber;

    @Enumerated(EnumType.STRING)  // important: store enum as string in DB
    private SeatStatus status;      // <-- this was missing

    private LocalDateTime heldUntil;

    @ManyToOne
    private MovieShow movieShow;

    // getters & setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }

    public LocalDateTime getHeldUntil() { return heldUntil; }
    public void setHeldUntil(LocalDateTime heldUntil) { this.heldUntil = heldUntil; }

    public MovieShow getMovieShow() { return movieShow; }
    public void setMovieShow(MovieShow movieShow) { this.movieShow = movieShow; }
}
