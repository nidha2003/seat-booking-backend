# Movie Show Seat Booking Backend

This project implements the backend logic for managing seat availability and booking for a movie show.
The focus is on correctness, concurrency safety, and handling real-world edge cases.

## Problem Scope
- Manage seat availability for a single movie show
- Prevent double booking under concurrent requests
- Handle temporary seat holds and expiry
- No UI, payment, or user authentication is included

---

## Seat States

Each seat can be in one of the following states:

- AVAILABLE – Seat is free to book
- HELD – Seat is temporarily locked for a user
- BOOKED – Seat is permanently booked

---

## Seat Hold Logic

When a user selects a seat:
1. A database transaction is started
2. The seat row is locked using `SELECT ... FOR UPDATE`
3. The seat is marked as `HELD`
4. A `held_until` timestamp is set (e.g., 5 minutes)

If the booking is not completed before `held_until`,
the seat becomes available again.

---

## Concurrency Handling

To prevent double booking:
- All seat updates happen inside transactions
- Row-level locking (`FOR UPDATE`) is used
- Only one transaction can modify a seat at a time

This guarantees that the same seat cannot be booked by multiple users.

---

## APIs (Example)

- `GET /shows/{id}/seats/availability`
- `POST /shows/{id}/seats/{seatNo}/hold`
- `POST /shows/{id}/seats/{seatNo}/book`

---

## Cleanup of Expired Holds

A cleanup job releases expired holds:

```sql
UPDATE seat
SET status = 'AVAILABLE',
    held_until = NULL
WHERE status = 'HELD'
  AND held_until < NOW();
```

This ensures system stability even if:
- Users abandon bookings
- Requests fail
- The system restarts

## Assumptions & Limitations
- Single database instance
- No payment or authentication logic
- One movie show at a time
- Cleanup job can be triggered manually or periodically

## Tech Stack
- Java
- Spring Boot
- PostgreSQL

