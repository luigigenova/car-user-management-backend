package com.desafio.controller;

import com.desafio.dto.CarRequestDTO;
import com.desafio.dto.CarResponseDTO;
import com.desafio.entity.Car;
import com.desafio.entity.User;
import com.desafio.service.ICarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Classe de teste unitário para o controlador {@link CarController}.
 * <p>
 * Testa as funcionalidades de criação, atualização, listagem e exclusão de carros.
 * </p>
 */
class CarControllerTest {

    /**
     * Mock do serviço de carros.
     */
    @Mock
    private ICarService carService;

    /**
     * Controlador de carros sendo testado.
     */
    @InjectMocks
    private CarController carController;

    /**
     * Mock do usuário autenticado.
     */
    @Mock
    private Principal principal;

    private Car car;
    private CarRequestDTO carRequestDTO;
    private User user;

    /**
     * Configuração inicial antes de cada teste.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);

        car = new Car();
        car.setId(1L);
        car.setColor("Yellow");
        car.setModel("Corolla");
        car.setYear(2021);
        car.setLicensePlate("ABC-1234");
        car.setUser(user);

        carRequestDTO = new CarRequestDTO("Corolla", "Toyota", 2021, "ABC-1234");

        when(principal.getName()).thenReturn("1");
    }

    /**
     * Testa a criação de um carro com sucesso.
     */
    @Test
    void deveCriarCarroComSucesso() {
        when(carService.saveCar(any(Car.class))).thenReturn(car);

        ResponseEntity<?> response = carController.createCar(carRequestDTO, principal);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof CarResponseDTO);
        CarResponseDTO responseBody = (CarResponseDTO) response.getBody();
        assertEquals("Yellow", responseBody.getColor());
        verify(carService, times(1)).saveCar(any(Car.class));
    }

    /**
     * Testa a criação de um carro com placa duplicada.
     */
    @Test
    void deveRetornarErroAoCriarCarroComPlacaDuplicada() {
        when(carService.existsByLicensePlate(car.getLicensePlate())).thenReturn(true);

        ResponseEntity<?> response = carController.createCar(carRequestDTO, principal);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("License plate already exists", responseBody.get("message"));
        assertEquals(409, responseBody.get("errorCode"));
        verify(carService, never()).saveCar(any(Car.class));
    }

    /**
     * Testa a atualização de um carro com sucesso.
     */
    @Test
    void deveAtualizarCarroComSucesso() {
        when(carService.updateCar(eq(1L), any(Car.class))).thenReturn(car);

        ResponseEntity<?> response = carController.updateCar(1L, carRequestDTO, principal);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof CarResponseDTO);
        CarResponseDTO responseBody = (CarResponseDTO) response.getBody();
        assertEquals("Toyota", responseBody.getColor());
        verify(carService, times(1)).updateCar(eq(1L), any(Car.class));
    }

    /**
     * Testa a atualização de um carro com placa duplicada.
     */
    @Test
    void deveRetornarErroAoAtualizarCarroComPlacaDuplicada() {
        when(carService.existsByLicensePlateAndIdNot(car.getLicensePlate(), car.getId())).thenReturn(true);

        ResponseEntity<?> response = carController.updateCar(1L, carRequestDTO, principal);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("License plate already exists", responseBody.get("message"));
        assertEquals(409, responseBody.get("errorCode"));
        verify(carService, never()).updateCar(anyLong(), any(Car.class));
    }

    /**
     * Testa a listagem de todos os carros do usuário autenticado.
     */
    @Test
    void deveListarCarrosDoUsuarioComSucesso() {
        when(carService.getCarsByUserId(1L)).thenReturn(List.of(car));

        ResponseEntity<?> response = carController.getCars(principal);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        List<CarResponseDTO> responseBody = (List<CarResponseDTO>) response.getBody();
        assertEquals(1, responseBody.size());
        assertEquals("Toyota", responseBody.get(0).getColor());
        verify(carService, times(1)).getCarsByUserId(1L);
    }

    /**
     * Testa a exclusão de um carro com sucesso.
     */
    @Test
    void deveDeletarCarroComSucesso() {
        doNothing().when(carService).deleteCar(1L, 1L);

        ResponseEntity<?> response = carController.deleteCar(1L, principal);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(carService, times(1)).deleteCar(1L, 1L);
    }

    /**
     * Testa a tentativa de exclusão de um carro com falha na autorização.
     */
    @Test
    void deveRetornarErroAoDeletarCarroSemAutorizacao() {
        doThrow(new IllegalArgumentException("Unauthorized")).when(carService).deleteCar(1L, 1L);

        ResponseEntity<?> response = carController.deleteCar(1L, principal);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("Unauthorized", responseBody.get("message"));
        assertEquals(401, responseBody.get("errorCode"));
        verify(carService, times(1)).deleteCar(1L, 1L);
    }
}
