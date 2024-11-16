package com.desafio.service.imp;

import com.desafio.entity.Car;
import com.desafio.repository.ICarRepository;
import com.desafio.service.ICarService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CarService implements ICarService {

    @Autowired
    private ICarRepository carRepository;

    @Override
    public List<Car> findAll() {
        return carRepository.findAll();
    }

    @Override
    public Optional<Car> findById(Long id) {
        return carRepository.findById(id);
    }

    @Override
    public Car save(Car car) {
        return carRepository.save(car);
    }

    @Override
    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }

    @Override
    public boolean existsByLicensePlate(String licensePlate) {
        return carRepository.existsByLicensePlate(licensePlate);
    }
}
