package org.example.uberbookingservice.dtos;

import lombok.*;
import org.example.uberproject.models.ExactLocation;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingDto {
    private Long passengerId;
    private ExactLocation startLocation;
    private ExactLocation endLocation;
}
