package com.desafio.service.impl;

import com.desafio.entity.Car;
import com.desafio.entity.User;
import com.desafio.repository.ICarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de teste para a implementação dos serviços relacionados à entidade {@link Car}.
 * <p>
 * Garante a cobertura de todas as funcionalidades da classe {@link CarServiceImpl},
 * aplicando boas práticas e garantindo robustez.
 * </p>
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
        Car car = createCar(1L, "ABC-1234", "Modelo X", "Yellow", 2021);

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
        Car car = createCar(null, "ABC-1234", "Modelo Y", "Blue", 2020);

        when(carRepository.existsByLicensePlate(car.getLicensePlate())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> carService.saveCar(car));

        assertEquals("A car with this license plate already exists.", exception.getMessage());
        verify(carRepository, never()).save(car);
    }

    /**
     * Testa a funcionalidade de atualizar um carro com sucesso.
     */
    @Test
    void deveAtualizarCarroComSucesso() {
        Car existingCar = createCar(1L, "XYZ-9876", "Modelo Atual", "Red", 2019);
        Car updatedCar = createCar(null, "XYZ-9876", "Modelo Atualizado", "Yellow", 2021);

        when(carRepository.findById(1L)).thenReturn(Optional.of(existingCar));
        when(carRepository.save(updatedCar)).thenReturn(updatedCar);

        Car result = carService.updateCar(1L, updatedCar);

        assertNotNull(result);
        assertEquals("XYZ-9876", result.getLicensePlate());
        assertEquals("Modelo Atualizado", result.getModel());
        verify(carRepository, times(1)).save(updatedCar);
    }

    /**
     * Testa a tentativa de atualizar um carro inexistente.
     */
    @Test
    void deveLancarExcecaoAoAtualizarCarroInexistente() {
        Car updatedCar = createCar(null, "XYZ-9876", "Modelo Atualizado", "Yellow", 2021);

        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> carService.updateCar(1L, updatedCar));

        assertEquals("Car not found for update.", exception.getMessage());
        verify(carRepository, never()).save(updatedCar);
    }

    /**
     * Testa a funcionalidade de buscar carros pelo ID do usuário.
     */
    @Test
    void deveBuscarCarrosPorUserId() {
        List<Car> cars = List.of(createCar(1L, "ABC-1234", "Modelo Z", "Green", 2022));

        when(carRepository.findByUserId(1L)).thenReturn(cars);

        List<Car> result = carService.getCarsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Modelo Z", result.get(0).getModel());
        verify(carRepository, times(1)).findByUserId(1L);
    }

    /**
     * Testa a funcionalidade de excluir um carro com sucesso.
     */
    @Test
    void deveExcluirCarroComSucesso() {
        User user = createUser(1L);
        Car car = createCar(1L, "XYZ-9876", "Modelo W", "Black", 2023, user);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        carService.deleteCar(1L, 1L);

        verify(carRepository, times(1)).deleteById(1L);
    }

    /**
     * Testa a tentativa de excluir um carro que não pertence ao usuário.
     */
    @Test
    void deveLancarExcecaoAoExcluirCarroDeOutroUsuario() {
        User user = createUser(2L);
        Car car = createCar(1L, "XYZ-9876", "Modelo W", "Black", 2023, user);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> carService.deleteCar(1L, 1L));

        assertEquals("Car not found or does not belong to the user.", exception.getMessage());
        verify(carRepository, never()).deleteById(1L);
    }

    /**
     * Testa a tentativa de excluir um carro inexistente.
     */
    @Test
    void deveLancarExcecaoAoExcluirCarroInexistente() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> carService.deleteCar(1L, 1L));

        assertEquals("Car not found or does not belong to the user.", exception.getMessage());
        verify(carRepository, never()).deleteById(1L);
    }

    /**
     * Testa a funcionalidade de buscar carros disponíveis.
     */
    @Test
    void deveBuscarCarrosDisponiveis() {
        List<Car> availableCars = List.of(createCar(1L, "FREE-123", "Modelo Livre", "White", 2020));

        when(carRepository.findAvailableCars()).thenReturn(availableCars);

        List<Car> result = carService.findAvailableCars();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("FREE-123", result.get(0).getLicensePlate());
        verify(carRepository, times(1)).findAvailableCars();
    }

    /**
     * Cria uma instância de {@link Car}.
     *
     * @param id           ID do carro.
     * @param licensePlate Placa do carro.
     * @param model        Modelo do carro.
     * @param color        Cor do carro.
     * @param year         Ano de fabricação.
     * @return Instância de {@link Car}.
     */
    private Car createCar(Long id, String licensePlate, String model, String color, int year) {
        return createCar(id, licensePlate, model, color, year, null);
    }

    /**
     * Cria uma instância de {@link Car} com um usuário associado.
     *
     * @param id           ID do carro.
     * @param licensePlate Placa do carro.
     * @param model        Modelo do carro.
     * @param color        Cor do carro.
     * @param year         Ano de fabricação.
     * @param user         Usuário associado.
     * @return Instância de {@link Car}.
     */
    private Car createCar(Long id, String licensePlate, String model, String color, int year, User user) {
        Car car = new Car();
        car.setId(id);
        car.setLicensePlate(licensePlate);
        car.setModel(model);
        car.setColor(color);
        car.setYear(year);
        car.setUser(user);
        return car;
    }

    /**
     * Cria uma instância de {@link User}.
     *
     * @param id ID do usuário.
     * @return Instância de {@link User}.
     */
    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}
