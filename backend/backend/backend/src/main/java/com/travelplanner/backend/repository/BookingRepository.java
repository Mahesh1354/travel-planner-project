package com.travelplanner.backend.repository;
import com.travelplanner.backend.entity.Booking;
import com.travelplanner.backend.entity.Trip;
import com.travelplanner.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find bookings by trip
    List<Booking> findByTrip(Trip trip);

    // Find bookings by trip ID
    List<Booking> findByTripId(Long tripId);

    // Find bookings by user
    List<Booking> findByUser(User user);

    // Find bookings by user ID
    List<Booking> findByUserId(Long userId);

    // Find bookings by type
    List<Booking> findByBookingType(String bookingType);

    // Find bookings by status
    List<Booking> findByBookingStatus(String bookingStatus);

    // Find bookings by trip and type
    List<Booking> findByTripAndBookingType(Trip trip, String bookingType);
}
