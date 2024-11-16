package com.desafio.service.imp;

import com.desafio.entity.Car;
import com.desafio.repository.ICarRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarServiceTest {

    @Mock
    private ICarRepository carRepository;

    @InjectMocks
    private CarService carService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExistsByLicensePlate() {
        when(carRepository.existsByLicensePlate("ABC1234")).thenReturn(true);
        
        boolean exists = carService.existsByLicensePlate("ABC1234");
        assertTrue(exists);
    }

    @Test
    void testSaveCar() {
        Car car = new Car();
        car.setModel("Toyota");

        when(carRepository.save(car)).thenReturn(car);

        Car savedCar = carService.save(car);
        assertEquals("Toyota", savedCar.getModel());
    }
}
