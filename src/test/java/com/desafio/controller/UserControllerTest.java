package com.desafio.controller;

import com.desafio.dto.request.UserRequestDTO;
import com.desafio.dto.response.UserResponseDTO;
import com.desafio.entity.Car;
import com.desafio.entity.User;
import com.desafio.service.ICarService;
import com.desafio.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Classe de teste para o controlador {@link UserController}.
 * <p>
 * Valida todos os endpoints relacionados ao gerenciamento de usuários.
 * Aplica conceitos de SOLID e boas práticas para robustez e clareza.
 * </p>
 */
class UserControllerTest {

    /**
     * Mock do serviço de usuários.
     */
    @Mock
    private IUserService userService;

    /**
     * Mock do serviço de carros.
     */
    @Mock
    private ICarService carService;

    /**
     * Controlador sendo testado.
     */
    @InjectMocks
    private UserController userController;

    /**
     * Configuração inicial dos mocks antes de cada teste.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testa o endpoint para listar todos os usuários com carros associados.
     */
    @Test
    void deveListarTodosOsUsuariosComCarros() {
        User user = createUser(1L, "user1", "user1@example.com");
        Car car = createCar(101L, "Model X", "Red", 2021, "ABC-1234", user);
        user.setCars(List.of(car));

        when(userService.findAllUsersWithCars()).thenReturn(List.of(UserResponseDTO.fromEntity(user)));

        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsersWithCars();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("user1", response.getBody().get(0).getLogin());
        verify(userService, times(1)).findAllUsersWithCars();
    }

    /**
     * Testa o endpoint que busca um usuário pelo ID com sucesso.
     */
    @Test
    void deveBuscarUsuarioPorIdComSucesso() {
        User user = createUser(1L, "user1", "user1@example.com");

        when(userService.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userController.getUserById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof UserResponseDTO);
        UserResponseDTO responseBody = (UserResponseDTO) response.getBody();
        assertEquals("user1", responseBody.getLogin());
        verify(userService, times(1)).findById(1L);
    }

    /**
     * Testa o endpoint que retorna erro 404 quando o usuário não é encontrado.
     */
    @Test
    void deveRetornar404AoBuscarUsuarioNaoExistente() {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.getUserById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("User not found", responseBody.get("message"));
        assertEquals(404, responseBody.get("errorCode"));
        verify(userService, times(1)).findById(1L);
    }

    /**
     * Testa o endpoint que cria um usuário com sucesso.
     */
    @Test
    void deveCriarUsuarioComSucesso() {
        // Criando um UserRequestDTO válido
        UserRequestDTO userRequest = new UserRequestDTO();
        userRequest.setLogin("user1");
        userRequest.setEmail("user1@example.com");
        userRequest.setPassword("password123");
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setBirthday("1990-01-01"); // Data de nascimento válida

        // Criando o objeto User retornado pelo serviço
        User user = new User();
        user.setId(1L);
        user.setLogin("user1");
        user.setEmail("user1@example.com");

        // Configurando os mocks
        when(userService.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(userService.existsByLogin(userRequest.getLogin())).thenReturn(false);
        when(userService.save(any(User.class))).thenReturn(user);

        // Executando o método do controlador
        ResponseEntity<?> response = userController.createUser(userRequest);

        // Verificações
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userService, times(1)).save(any(User.class));
    }

    /**
     * Testa o endpoint que exclui um usuário com sucesso.
     */
    @Test
    void deveExcluirUsuarioComSucesso() {
        when(userService.findById(1L)).thenReturn(Optional.of(new User()));

        ResponseEntity<?> response = userController.deleteUser(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteById(1L);
    }

    /**
     * Testa o endpoint que retorna erro 404 ao tentar excluir um usuário
     * inexistente.
     */
    @Test
    void deveRetornar404AoExcluirUsuarioNaoExistente() {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.deleteUser(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("User not found", responseBody.get("message"));
        assertEquals(404, responseBody.get("errorCode"));
        verify(userService, never()).deleteById(1L);
    }

    /**
     * Cria uma instância de {@link User}.
     *
     * @param id    ID do usuário.
     * @param login Login do usuário.
     * @param email Email do usuário.
     * @return Instância de {@link User}.
     */
    private User createUser(Long id, String login, String email) {
        User user = new User();
        user.setId(id);
        user.setLogin(login);
        user.setEmail(email);
        return user;
    }

    /**
     * Cria uma instância de {@link Car}.
     *
     * @param id           ID do carro.
     * @param model        Modelo do carro.
     * @param color        Cor do carro.
     * @param year         Ano do carro.
     * @param licensePlate Placa do carro.
     * @param user         Usuário associado.
     * @return Instância de {@link Car}.
     */
    private Car createCar(Long id, String model, String color, int year, String licensePlate, User user) {
        Car car = new Car();
        car.setId(id);
        car.setModel(model);
        car.setColor(color);
        car.setYear(year);
        car.setLicensePlate(licensePlate);
        car.setUser(user);
        return car;
    }

    /**
     * Cria uma instância de {@link UserRequestDTO}.
     *
     * @param login    Login do usuário.
     * @param email    Email do usuário.
     * @param password Senha do usuário.
     * @return Instância de {@link UserRequestDTO}.
     */
    private UserRequestDTO createUserRequestDTO(String login, String email, String password) {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setLogin(login);
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }
}
