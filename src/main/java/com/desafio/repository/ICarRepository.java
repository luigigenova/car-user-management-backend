package com.desafio.repository;

import com.desafio.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICarRepository extends JpaRepository<Car, Long> {
    boolean existsByLicensePlate(String licensePlate);
}
