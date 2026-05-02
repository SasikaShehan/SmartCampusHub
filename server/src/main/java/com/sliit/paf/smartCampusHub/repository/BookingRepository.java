package com.sliit.paf.smartCampusHub.repository;

import com.sliit.paf.smartCampusHub.model.Booking;
import com.sliit.paf.smartCampusHub.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Get bookings by user
    List<Booking> findByUserId(Long userId);

    // Get bookings by status
    List<Booking> findByStatus(BookingStatus status);

    // Get bookings by facility
    List<Booking> findByFacilityId(Long facilityId);

    // Get pending bookings
    List<Booking> findByStatusOrderByCreatedAtAsc(BookingStatus status);

    // Check for conflicting bookings
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE " +
           "b.facility.id = :facilityId AND " +
           "b.status NOT IN (:excludedStatuses) AND " +
           "((b.startTime < :endTime AND b.endTime > :startTime))")
    boolean existsConflict(@Param("facilityId") Long facilityId,
                          @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime,
                          @Param("excludedStatuses") List<BookingStatus> excludedStatuses);

    // Check for conflicting bookings EXCLUDING self
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE " +
           "b.facility.id = :facilityId AND " +
           "b.id != :bookingId AND " +
           "b.status NOT IN (:excludedStatuses) AND " +
           "((b.startTime < :endTime AND b.endTime > :startTime))")
    boolean existsConflictExcludingSelf(@Param("facilityId") Long facilityId,
                                        @Param("bookingId") Long bookingId,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime,
                                        @Param("excludedStatuses") List<BookingStatus> excludedStatuses);

    // Get upcoming bookings for a facility
    @Query("SELECT b FROM Booking b WHERE b.facility.id = :facilityId " +
           "AND b.startTime >= :now AND b.status = 'APPROVED' " +
           "ORDER BY b.startTime ASC")
    List<Booking> findUpcomingBookings(@Param("facilityId") Long facilityId,
                                       @Param("now") LocalDateTime now);
}
