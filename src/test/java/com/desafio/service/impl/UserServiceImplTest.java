package com.desafio.service.impl;

import com.desafio.entity.User;
import com.desafio.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de teste para {@link UserServiceImpl}.
 * <p>
 * Verifica o comportamento dos métodos da classe de serviço, garantindo que
 * todas as operações de negócios sejam realizadas corretamente.
 */
class UserServiceImplTest {

    /** Mock do repositório de usuários */
    @Mock
    private IUserRepository userRepository;

    /** Mock do codificador de senha */
    @Mock
    private PasswordEncoder passwordEncoder;

    /** Serviço sendo testado */
    @InjectMocks
    private UserServiceImpl userService;

    /** Instância de um usuário para os testes */
    private User user;

    /**
     * Configuração inicial dos mocks e objetos de teste.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setLogin("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
    }

    /**
     * Testa a busca de um usuário por ID.
     */
    @Test
    void deveRetornarUsuarioPorId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getLogin());
        verify(userRepository, times(1)).findById(1L);
    }

    /**
     * Testa a tentativa de buscar um usuário inexistente por ID.
     */
    @Test
    void deveRetornarVazioParaUsuarioInexistente() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(1L);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(1L);
    }

    /**
     * Testa o salvamento de um novo usuário com sucesso.
     */
    @Test
    void deveSalvarUsuarioComSucesso() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.save(user);

        assertNotNull(savedUser);
        assertEquals("encodedPassword", savedUser.getPassword());
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
}

    /**
     * Testa a tentativa de salvar um usuário com um email já existente.
     */
    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class, () -> userService.save(user));
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Testa a exclusão de um usuário pelo ID.
     */
    @Test
    void deveExcluirUsuarioPorId() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    /**
     * Testa a verificação de existência de um email já cadastrado.
     */
    @Test
    void deveVerificarSeEmailJaExiste() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean exists = userService.existsByEmail("test@example.com");

        assertTrue(exists);
        verify(userRepository, times(1)).existsByEmail("test@example.com");
    }

    /**
     * Testa a verificação de existência de um login já cadastrado.
     */
    @Test
    void deveVerificarSeLoginJaExiste() {
        when(userRepository.existsByLogin("testuser")).thenReturn(true);

        boolean exists = userService.existsByLogin("testuser");

        assertTrue(exists);
        verify(userRepository, times(1)).existsByLogin("testuser");
    }
}
