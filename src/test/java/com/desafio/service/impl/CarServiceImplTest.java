package com.desafio.service.impl;

import com.desafio.entity.Car;
import com.desafio.entity.User;
import com.desafio.repository.ICarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de teste para a implementação dos serviços relacionados à entidade Car.
 * Garante a cobertura de todas as funcionalidades da classe {@link CarServiceImpl}.
 */
class CarServiceImplTest {

    /**
     * Mock do repositório de carros.
     */
    @Mock
    private ICarRepository carRepository;

    /**
     * Instância do serviço sendo testada.
     */
    @InjectMocks
    private CarServiceImpl carService;

    /**
     * Configuração inicial dos mocks antes de cada teste.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testa a funcionalidade de salvar um carro com sucesso.
     */
    @Test
    void deveSalvarCarroComSucesso() {
        Car car = new Car();
        car.setId(1L);
        car.setLicensePlate("ABC-1234");
        car.setModel("Modelo X");
        car.setColor("Yellow");

        when(carRepository.existsByLicensePlate(car.getLicensePlate())).thenReturn(false);
        when(carRepository.save(car)).thenReturn(car);

        Car savedCar = carService.saveCar(car);

        assertNotNull(savedCar);
        assertEquals("ABC-1234", savedCar.getLicensePlate());
        verify(carRepository, times(1)).save(car);
    }

    /**
     * Testa a tentativa de salvar um carro com uma placa já existente.
     */
    @Test
    void deveLancarExcecaoAoSalvarCarroComPlacaJaExistente() {
        Car car = new Car();
        car.setLicensePlate("ABC-1234");

        when(carRepository.existsByLicensePlate(car.getLicensePlate())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> carService.saveCar(car));

        assertEquals("Já existe um carro com esta placa.", exception.getMessage());
        verify(carRepository, never()).save(car);
    }

    /**
     * Testa a funcionalidade de atualizar um carro com sucesso.
     */
    @Test
    void deveAtualizarCarroComSucesso() {
        Car existingCar = new Car();
        existingCar.setId(1L);

        Car updatedCar = new Car();
        updatedCar.setLicensePlate("XYZ-9876");
        updatedCar.setModel("Modelo Atualizado");
        updatedCar.setColor("Yellow");

        when(carRepository.findById(1L)).thenReturn(Optional.of(existingCar));
        when(carRepository.save(updatedCar)).thenReturn(updatedCar);

        Car result = carService.updateCar(1L, updatedCar);

        assertNotNull(result);
        assertEquals("XYZ-9876", result.getLicensePlate());
        verify(carRepository, times(1)).save(updatedCar);
    }

    /**
     * Testa a tentativa de atualizar um carro inexistente.
     */
    @Test
    void deveLancarExcecaoAoAtualizarCarroInexistente() {
        Car updatedCar = new Car();
        updatedCar.setLicensePlate("XYZ-9876");

        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> carService.updateCar(1L, updatedCar));

        assertEquals("Carro não encontrado para atualização.", exception.getMessage());
        verify(carRepository, never()).save(updatedCar);
    }

    /**
     * Testa a funcionalidade de buscar carros pelo ID do usuário.
     */
    @Test
    void deveBuscarCarrosPorUserId() {
        List<Car> cars = new ArrayList<>();
        Car car1 = new Car();
        car1.setId(1L);
        car1.setLicensePlate("ABC-1234");
        cars.add(car1);

        when(carRepository.findByUserId(1L)).thenReturn(cars);

        List<Car> result = carService.getCarsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(carRepository, times(1)).findByUserId(1L);
    }

    /**
     * Testa a funcionalidade de excluir um carro com sucesso.
     */
    @Test
    void deveExcluirCarroComSucesso() {
        User user = new User();
        user.setId(1L);

        Car car = new Car();
        car.setId(1L);
        car.setUser(user);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        carService.deleteCar(1L, 1L);

        verify(carRepository, times(1)).deleteById(1L);
    }

    /**
     * Testa a tentativa de excluir um carro que não pertence ao usuário.
     */
    @Test
    void deveLancarExcecaoAoExcluirCarroDeOutroUsuario() {
        User user = new User();
        user.setId(2L);

        Car car = new Car();
        car.setId(1L);
        car.setUser(user);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> carService.deleteCar(1L, 1L));

        assertEquals("Carro não encontrado ou não pertence ao usuário.", exception.getMessage());
        verify(carRepository, never()).deleteById(1L);
    }

    /**
     * Testa a tentativa de excluir um carro inexistente.
     */
    @Test
    void deveLancarExcecaoAoExcluirCarroInexistente() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> carService.deleteCar(1L, 1L));

        assertEquals("Carro não encontrado ou não pertence ao usuário.", exception.getMessage());
        verify(carRepository, never()).deleteById(1L);
    }
}
