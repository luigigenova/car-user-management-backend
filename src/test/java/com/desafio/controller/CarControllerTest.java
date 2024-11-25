package com.desafio.controller;

import com.desafio.dto.request.CarRequestDTO;
import com.desafio.dto.response.CarResponseDTO;
import com.desafio.entity.Car;
import com.desafio.entity.User;
import com.desafio.exception.DuplicateLicensePlateException;
import com.desafio.service.ICarService;
import com.desafio.service.IUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Teste unitário para o controlador {@link CarController}.
 * <p>
 * Valida as operações de CRUD no contexto de carros para usuários autenticados.
 * Aplica conceitos de S.O.L.I.D. e boas práticas para garantir robustez.
 * </p>
 */
class CarControllerTest {

    /**
     * Mock para o serviço de gerenciamento de carros.
     */
    @Mock
    private ICarService carService;

    @Mock
    private IUserService userService;

    /**
     * Controlador de carros com dependências injetadas.
     */
    @InjectMocks
    private CarController carController;

    /**
     * Mock para o usuário autenticado.
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

        user = createUser(1L);
        car = createCar(1L, "Corolla", "Toyota", 2021, "Yellow", "ABC-1234", user);
        carRequestDTO = createCarRequestDTO("Corolla", "Toyota", 2021, "ABC-1234");

        when(principal.getName()).thenReturn("1");
    }

    /**
     * Testa a criação de um carro com sucesso.
     */
    @Test
    void deveCriarCarroComSucesso() {
        // Mock do Authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        // Configurando o SecurityContext com o mock
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Configurando o nome do usuário no Authentication
        when(authentication.getName()).thenReturn("testuser");

        // Mock do usuário
        User user = new User();
        user.setId(1L);

        // Configurando o mock do userService
        when(userService.findByUsername("testuser")).thenReturn(user);

        // Configurando o mock do carService
        when(carService.saveCar(any(Car.class))).thenReturn(car);

        // Executando o teste
        ResponseEntity<?> response = carController.createCar(carRequestDTO);

        // Verificações
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("Car created successfully", responseBody.get("message"));
        verify(carService, times(1)).saveCar(any(Car.class));

        // Resetando o SecurityContext após o teste
        SecurityContextHolder.clearContext();
    }

    /**
     * Testa a criação de um carro com uma placa duplicada.
     */
    @Test
    void deveRetornarErroAoCriarCarroComPlacaDuplicada() {
        // Mock do Authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        // Configurando o SecurityContext com o mock
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Configurando o nome do usuário no Authentication
        when(authentication.getName()).thenReturn("testuser");

        // Mock do usuário
        User user = new User();
        user.setId(1L);

        // Configurando o mock do userService
        when(userService.findByUsername("testuser")).thenReturn(user);

        // Simulando exceção de placa duplicada no carService
        doThrow(new DuplicateLicensePlateException("License plate already exists"))
                .when(carService).saveCar(any(Car.class));

        // Executando o teste
        ResponseEntity<?> response = carController.createCar(carRequestDTO);

        // Verificações
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("License plate already exists", responseBody.get("message"));
        verify(carService, times(1)).saveCar(any(Car.class));

        // Resetando o SecurityContext após o teste
        SecurityContextHolder.clearContext();
    }

    /**
     * Testa a listagem de carros do usuário autenticado.
     */
    @Test
    void deveListarCarrosDoUsuarioComSucesso() {
        // Mock do Authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        // Configurando o SecurityContext com o mock
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Configurando o nome do usuário no Authentication
        when(authentication.getName()).thenReturn("testuser");

        // Mock do usuário
        User user = new User();
        user.setId(1L);

        // Configuração do mock do userService
        when(userService.findByUsername("testuser")).thenReturn(user);

        // Configuração do mock do carService
        when(carService.getCarsByUserId(1L)).thenReturn(List.of(car));

        // Executando o teste
        ResponseEntity<?> response = carController.getCars();

        // Verificações
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        List<CarResponseDTO> responseBody = (List<CarResponseDTO>) response.getBody();
        assertEquals(1, responseBody.size());
        verify(carService, times(1)).getCarsByUserId(1L);

        // Resetando o SecurityContext após o teste
        SecurityContextHolder.clearContext();
    }

    /**
     * Testa a exclusão de um carro com sucesso.
     */
    @Test
    void deveDeletarCarroComSucesso() {
        // Mock do Authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        // Configurando o SecurityContext com o mock
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Configurando o nome do usuário no Authentication
        when(authentication.getName()).thenReturn("testuser");

        // Mock do usuário
        User user = new User();
        user.setId(1L);

        // Mock do carro
        Car car = new Car();
        car.setId(1L);
        car.setUser(user);

        // Configuração do mock do userService
        when(userService.findByUsername("testuser")).thenReturn(user);

        // Configuração do mock do carService
        when(carService.existsByIdAndUserId(1L, 1L)).thenReturn(true);
        doNothing().when(carService).deleteCar(1L, 1L);

        // Executando o teste
        ResponseEntity<?> response = carController.deleteCar(1L);

        // Verificações
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(carService, times(1)).deleteCar(1L, 1L);

        // Resetando o SecurityContext após o teste
        SecurityContextHolder.clearContext();
    }

    /**
     * Testa a tentativa de exclusão de um carro inexistente.
     */
    @Test
    void deveRetornarErroAoDeletarCarroInexistente() {
        // Mock do Authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        // Configurando o SecurityContext com o mock
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Configurando o nome do usuário no Authentication
        when(authentication.getName()).thenReturn("testuser");

        // Mock do usuário
        User user = new User();
        user.setId(1L);

        // Configurando o mock do userService
        when(userService.findByUsername("testuser")).thenReturn(user);

        // Configurando o mock do carService
        when(carService.existsByIdAndUserId(1L, 1L)).thenReturn(false);

        // Executando o teste
        ResponseEntity<?> response = carController.deleteCar(1L);

        // Verificações
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("Car not found", responseBody.get("message"));

        // Garantir que deleteCar não foi chamado
        verify(carService, never()).deleteCar(anyLong(), anyLong());

        // Resetando o SecurityContext após o teste
        SecurityContextHolder.clearContext();
    }

    /**
     * Cria uma instância de {@link User} para os testes.
     *
     * @param userId ID do usuário.
     * @return Instância de {@link User}.
     */
    private User createUser(Long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    /**
     * Cria uma instância de {@link Car} para os testes.
     *
     * @param id           ID do carro.
     * @param model        Modelo do carro.
     * @param brand        Marca do carro.
     * @param year         Ano de fabricação.
     * @param color        Cor do carro.
     * @param licensePlate Placa do carro.
     * @param user         Usuário associado.
     * @return Instância de {@link Car}.
     */
    private Car createCar(Long id, String model, String brand, int year, String color, String licensePlate, User user) {
        Car car = new Car();
        car.setId(id);
        car.setModel(model);
        car.setYear(year);
        car.setColor(color);
        car.setLicensePlate(licensePlate);
        car.setUser(user);
        return car;
    }

    /**
     * Cria uma instância de {@link CarRequestDTO} para os testes.
     *
     * @param model        Modelo do carro.
     * @param brand        Marca do carro.
     * @param year         Ano de fabricação.
     * @param licensePlate Placa do carro.
     * @return Instância de {@link CarRequestDTO}.
     */
    private CarRequestDTO createCarRequestDTO(String model, String brand, int year, String licensePlate) {
        return new CarRequestDTO(model, brand, year, licensePlate);
    }
}
