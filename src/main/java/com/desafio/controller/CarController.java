package com.desafio.controller;

import com.desafio.dto.request.CarRequestDTO;
import com.desafio.dto.response.CarResponseDTO;
import com.desafio.entity.Car;
import com.desafio.entity.User;
import com.desafio.exception.DuplicateLicensePlateException;
import com.desafio.service.ICarService;
import com.desafio.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller responsible for managing car operations.
 */
@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final ICarService carService;
    private final IUserService userService;

    @Autowired
    public CarController(ICarService carService, IUserService userService) {
        this.carService = carService;
        this.userService = userService;
    }

    /**
     * Creates a new car for the authenticated user.
     *
     * @param carRequestDTO DTO containing car data.
     * @return ResponseEntity with success or error message.
     */
    @PostMapping
    @Operation(summary = "Create car", description = "Creates a new car for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Car created successfully"),
            @ApiResponse(responseCode = "409", description = "License plate already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid fields")
    })
    public ResponseEntity<?> createCar(@RequestBody CarRequestDTO carRequestDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            Car car = carRequestDTO.toEntity();
            car.setUser(user);

            carService.saveCar(car);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Car created successfully"));
        } catch (DuplicateLicensePlateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid fields"));
        }
    }

    /**
     * Lists all cars for the authenticated user.
     *
     * @return ResponseEntity with a list of cars.
     */
    @GetMapping
    @Operation(summary = "List cars", description = "Lists all cars for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cars retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getCars() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);
        List<CarResponseDTO> cars = carService.getCarsByUserId(user.getId())
                .stream()
                .map(CarResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(cars);
    }

    /**
     * Deletes a car by its ID for the authenticated user.
     *
     * @param id Car ID.
     * @return ResponseEntity indicating success or failure.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete car", description = "Deletes a car for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Car deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            if (!carService.existsByIdAndUserId(id, user.getId())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Car not found"));
            }

            carService.deleteCar(id, user.getId());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }
}
