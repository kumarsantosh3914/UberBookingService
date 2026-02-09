package org.example.uberbookingservice.services;

import org.example.uberbookingservice.dtos.CreateBookingDto;
import org.example.uberbookingservice.dtos.CreateBookingResponseDto;

public interface BookingService {
    CreateBookingResponseDto createBooking(CreateBookingDto bookingDetails);
}
