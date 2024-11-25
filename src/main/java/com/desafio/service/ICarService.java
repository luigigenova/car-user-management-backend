package com.desafio.service;

import com.desafio.entity.Car;
import java.util.List;
import java.util.Optional;

/**
 * Interface defining services related to the Car entity.
 * Adheres to the SOLID principles, especially Interface Segregation,
 * by defining specific methods for car manipulation.
 */
public interface ICarService {

    /**
     * Saves a new car in the system.
     *
     * @param car Car object to be saved.
     * @return The saved car.
     */
    Car saveCar(Car car);

    /**
     * Updates an existing car in the system.
     *
     * @param id  ID of the car to be updated.
     * @param car Car object containing the new data.
     * @return The updated car.
     */
    Car updateCar(Long id, Car car);

    /**
     * Retrieves all cars associated with a specific user.
     *
     * @param userId User ID.
     * @return List of user's cars.
     */
    List<Car> getCarsByUserId(Long userId);

    /**
     * Removes a car associated with a specific user.
     *
     * @param id     Car ID to be removed.
     * @param userId User ID of the owner.
     */
    void deleteCar(Long id, Long userId);

    /**
     * Checks if a car with the given license plate exists.
     *
     * @param licensePlate License plate of the car.
     * @return true if the license plate already exists, false otherwise.
     */
    boolean existsByLicensePlate(String licensePlate);

    /**
     * Checks if a car with the given license plate exists, excluding a specific ID.
     *
     * @param licensePlate License plate of the car.
     * @param id           Car ID to be excluded from the check.
     * @return true if the license plate already exists, false otherwise.
     */
    boolean existsByLicensePlateAndIdNot(String licensePlate, Long id);

    /**
     * Finds a car by its ID.
     *
     * @param id Car ID.
     * @return Optional containing the car if found, empty otherwise.
     */
    Optional<Car> findById(Long id);

    /**
     * Retrieves all cars not associated with any user.
     *
     * @return List of available cars.
     */
    List<Car> findAvailableCars();

    /**
     * Checks if a car with a specific ID belongs to a specific user.
     *
     * @param id     Car ID.
     * @param userId User ID.
     * @return true if the car belongs to the user, false otherwise.
     */
    boolean existsByIdAndUserId(Long id, Long userId);
}
