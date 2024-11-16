package com.desafio.service;

import com.desafio.entity.Car;
import java.util.List;
import java.util.Optional;

public interface ICarService {
    List<Car> findAll();
    Optional<Car> findById(Long id);
    Car save(Car car);
    void deleteById(Long id);
    boolean existsByLicensePlate(String licensePlate);
}
