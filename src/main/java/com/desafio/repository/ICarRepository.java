package com.desafio.repository;

import com.desafio.entity.Car;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing Car entity data.
 * Provides methods for CRUD operations and custom queries.
 */
@Repository
public interface ICarRepository extends JpaRepository<Car, Long> {

    /**
     * Retrieves all cars associated with a specific user.
     *
     * @param userId User ID.
     * @return List of cars associated with the user.
     */
    List<Car> findByUserId(Long userId);

    /**
     * Checks if a car with the given license plate exists.
     *
     * @param licensePlate License plate of the car.
     * @return true if the license plate exists, false otherwise.
     */
    boolean existsByLicensePlate(String licensePlate);

    /**
     * Checks if a car with the given license plate exists, excluding a specific car ID.
     *
     * @param licensePlate License plate of the car.
     * @param id           ID of the car to exclude.
     * @return true if the license plate exists, false otherwise.
     */
    boolean existsByLicensePlateAndIdNot(String licensePlate, Long id);

    /**
     * Checks if a car with a specific ID belongs to a specific user.
     *
     * @param id     Car ID.
     * @param userId User ID.
     * @return true if the car belongs to the user, false otherwise.
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Car c WHERE c.id = :id AND c.user.id = :userId")
    boolean existsByIdAndUserId(Long id, Long userId);

    /**
     * Retrieves all cars not associated with any user.
     *
     * @return List of cars not associated with any user.
     */
    @Query("SELECT c FROM Car c WHERE c.user IS NULL")
    List<Car> findAvailableCars();
}
