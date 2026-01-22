package com.example.seatbooking.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class MovieShow {

    @Id
    @GeneratedValue
    private UUID id;

    private String movieName;
    private int totalSeats;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getMovieName() { return movieName; }
    public void setMovieName(String movieName) { this.movieName = movieName; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
}
