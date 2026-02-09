package org.example.uberbookingservice.dtos;

import lombok.*;
import org.example.uberproject.models.BookingStatus;
import org.example.uberproject.models.Driver;

import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingResponseDto {
    private Long bookingId;
    private BookingStatus status;
    private Optional<Driver> driver;
}
