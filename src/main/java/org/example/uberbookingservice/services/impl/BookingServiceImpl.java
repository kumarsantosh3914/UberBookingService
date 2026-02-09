package org.example.uberbookingservice.services.impl;

import org.example.uberbookingservice.apis.LocationServiceApi;
import org.example.uberbookingservice.dtos.CreateBookingDto;
import org.example.uberbookingservice.dtos.CreateBookingResponseDto;
import org.example.uberbookingservice.dtos.DriverLocationDto;
import org.example.uberbookingservice.dtos.NearbyDriversRequestDto;
import org.example.uberbookingservice.repositories.BookingRepository;
import org.example.uberbookingservice.repositories.PassengerRepository;
import org.example.uberbookingservice.services.BookingService;
import org.example.uberproject.models.Booking;
import org.example.uberproject.models.BookingStatus;
import org.example.uberproject.models.Passenger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;

    private final RestTemplate restTemplate;

    // private static final String LOCATION_SERVICE_URL = "http://localhost:7777";

    private final LocationServiceApi locationServiceApi;

    public BookingServiceImpl(PassengerRepository passengerRepository, BookingRepository bookingRepository, LocationServiceApi locationServiceApi) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.restTemplate = new RestTemplate();
        this.locationServiceApi = locationServiceApi;
    }

    @Override
    public CreateBookingResponseDto createBooking(CreateBookingDto bookingDetails) {
        Optional<Passenger> passenger = passengerRepository.findById(bookingDetails.getPassengerId());
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.ASSIGNING_DRIVER)
                .startLocation(bookingDetails.getStartLocation())
                .passenger(passenger.get())
                .build();

        Booking newBooking = bookingRepository.save(booking);

        // make an api call to location service to get nearby drivers
        NearbyDriversRequestDto request = NearbyDriversRequestDto.builder()
                .latitude(bookingDetails.getStartLocation().getLatitude())
                .longitude(bookingDetails.getStartLocation().getLongitude())
                .build();

        processNearbyDriversAsync(request);
//
//        ResponseEntity<DriverLocationDto[]> result = restTemplate.postForEntity(LOCATION_SERVICE_URL + "api/v1/locations/nearby/drivers", request, DriverLocationDto[].class);
//        if(result.getStatusCode().is2xxSuccessful() && result.getBody() != null) {
//            List<DriverLocationDto> driverLocations = Arrays.asList(result.getBody());
//            driverLocations.forEach(driverLocationDto -> {
//                System.out.println(driverLocationDto.getDriverId() + " " + driverLocationDto.getLatitude() + " " + driverLocationDto.getLongitude());
//            });
//        }

        processNearbyDriversAsync(request);

        return CreateBookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .bookingStatus(newBooking.getBookingStatus().toString())
                .build();
    }

    private void processNearbyDriversAsync(NearbyDriversRequestDto requestDto) {
        Call<DriverLocationDto[]> call = locationServiceApi.getNearbyDrivers(requestDto);

        call.enqueue(new Callback<DriverLocationDto[]>() {
            @Override
            public void onResponse(Call<DriverLocationDto[]> call, Response<DriverLocationDto[]> response) {
                try {
                    Thread.sleep(5000); // Simulate some processing time
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if(response.isSuccessful() && response.body() != null) {
                    List<DriverLocationDto> driverLocations = Arrays.asList(response.body());
                    driverLocations.forEach(driverLocationDto -> {
                        System.out.println(driverLocationDto.getDriverId() + " " + driverLocationDto.getLatitude() + " " + driverLocationDto.getLongitude());
                    });
                } else {
                    System.out.println("Failed to fetch nearby drivers. Response code: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<DriverLocationDto[]> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
