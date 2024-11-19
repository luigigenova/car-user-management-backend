package com.desafio.controller;

import com.desafio.dto.UserResponseDTO;
import com.desafio.entity.Car;
import com.desafio.entity.User;
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
import static org.mockito.Mockito.*;

/**
 * Classe de teste para o controlador {@link UserController}.
 * <p>
 * Valida os endpoints relacionados ao gerenciamento de usuários,
 * com foco em cenários de sucesso e falha.
 * </p>
 */
class UserControllerTest {

    /**
     * Mock do serviço de usuários.
     */
    @Mock
    private IUserService userService;

    /**
     * Instância do controlador sendo testada.
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
     * Testa o endpoint para listar todos os usuários.
     */
    @Test
    void deveListarTodosOsUsuarios() {
        User user = new User();
        user.setId(1L);
        user.setLogin("user1");
        user.setEmail("user1@example.com");

        Car car = new Car();
        car.setId(101L);
        car.setModel("Model X");
        car.setColor("Yellow");
        user.setCars(List.of(car));

        when(userService.findAll()).thenReturn(List.of(user));

        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsers();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("user1", response.getBody().get(0).getLogin());
        assertEquals("Model X", response.getBody().get(0).getCars().get(0).getModel());
        verify(userService, times(1)).findAll();
    }

    /**
     * Testa o endpoint que busca um usuário pelo ID com sucesso.
     */
    @Test
    void deveBuscarUsuarioPorIdComSucesso() {
        User user = new User();
        user.setId(1L);
        user.setLogin("user1");
        user.setEmail("user1@example.com");

        when(userService.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userController.getUserById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof User);
        User responseBody = (User) response.getBody();
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
     * Testa o endpoint que atualiza um usuário com sucesso.
     */
    @Test
    void deveAtualizarUsuarioComSucesso() {
        User user = new User();
        user.setId(1L);
        user.setLogin("user1");
        user.setEmail("user1@example.com");

        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(userService.save(user)).thenReturn(user);

        ResponseEntity<?> response = userController.updateUser(1L, user);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof User);
        verify(userService, times(1)).save(user);
    }

    /**
     * Testa o endpoint que retorna erro 404 ao tentar atualizar um usuário inexistente.
     */
    @Test
    void deveRetornar404AoAtualizarUsuarioNaoExistente() {
        User user = new User();
        user.setId(1L);
        user.setLogin("user1");
        user.setEmail("user1@example.com");

        when(userService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.updateUser(1L, user);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("User not found", responseBody.get("message"));
        assertEquals(404, responseBody.get("errorCode"));
        verify(userService, never()).save(user);
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
     * Testa o endpoint que retorna erro 404 ao tentar excluir um usuário inexistente.
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
}
