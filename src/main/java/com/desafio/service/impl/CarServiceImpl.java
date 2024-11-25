package com.desafio.service.impl;

import com.desafio.entity.Car;
import com.desafio.repository.ICarRepository;
import com.desafio.service.ICarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of services related to the Car entity.
 * Adheres to the Single Responsibility Principle (SRP) by encapsulating car
 * manipulation business logic.
 */
@Service
public class CarServiceImpl implements ICarService {

    private final ICarRepository carRepository;

    /**
     * Constructor for dependency injection of the car repository.
     *
     * @param carRepository Car repository.
     */
    @Autowired
    public CarServiceImpl(ICarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public Car saveCar(Car car) {
        if (carRepository.existsByLicensePlate(car.getLicensePlate())) {
            throw new IllegalArgumentException("A car with this license plate already exists.");
        }
        return carRepository.save(car);
    }

    @Override
    public Car updateCar(Long id, Car car) {
        Optional<Car> existingCar = carRepository.findById(id);
        if (existingCar.isEmpty()) {
            throw new IllegalArgumentException("Car not found for update.");
        }
        car.setId(id);
        return carRepository.save(car);
    }

    @Override
    public List<Car> getCarsByUserId(Long userId) {
        return carRepository.findByUserId(userId);
    }

    @Override
    public void deleteCar(Long id, Long userId) {
        Optional<Car> car = carRepository.findById(id);
        if (car.isEmpty() || !car.get().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Car not found or does not belong to the user.");
        }
        carRepository.deleteById(id);
    }

    @Override
    public boolean existsByLicensePlate(String licensePlate) {
        return carRepository.existsByLicensePlate(licensePlate);
    }

    @Override
    public boolean existsByLicensePlateAndIdNot(String licensePlate, Long id) {
        return carRepository.existsByLicensePlateAndIdNot(licensePlate, id);
    }

    @Override
    public Optional<Car> findById(Long id) {
        return carRepository.findById(id);
    }

    @Override
    public List<Car> findAvailableCars() {
        return carRepository.findAvailableCars();
    }

    @Override
    public boolean existsByIdAndUserId(Long id, Long userId) {
        return carRepository.existsByIdAndUserId(id, userId);
    }
}
