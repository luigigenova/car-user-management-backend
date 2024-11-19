package com.desafio.service.impl;

import com.desafio.entity.Car;
import com.desafio.repository.ICarRepository;
import com.desafio.service.ICarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementação dos serviços relacionados à entidade Car.
 * Segue o princípio de responsabilidade única (SRP) do SOLID,
 * encapsulando a lógica de negócios de manipulação de carros.
 */
@Service
public class CarServiceImpl implements ICarService {

    private final ICarRepository carRepository;

    /**
     * Construtor para injeção de dependência do repositório de carros.
     *
     * @param carRepository Repositório de carros.
     */
    @Autowired
    public CarServiceImpl(ICarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public Car saveCar(Car car) {
        if (carRepository.existsByLicensePlate(car.getLicensePlate())) {
            throw new IllegalArgumentException("Já existe um carro com esta placa.");
        }
        return carRepository.save(car);
    }

    @Override
    public Car updateCar(Long id, Car car) {
        Optional<Car> existingCar = carRepository.findById(id);
        if (existingCar.isEmpty()) {
            throw new IllegalArgumentException("Carro não encontrado para atualização.");
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
            throw new IllegalArgumentException("Carro não encontrado ou não pertence ao usuário.");
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
}
