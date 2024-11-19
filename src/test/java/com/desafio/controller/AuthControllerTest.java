package com.desafio.controller;

import com.desafio.dto.AuthRequestDTO;
import com.desafio.entity.User;
import com.desafio.service.impl.MyUserDetailsService;
import com.desafio.service.impl.UserServiceImpl;
import com.desafio.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Classe de teste unitário para o controlador {@link AuthController}.
 * <p>
 * Realiza a validação dos endpoints relacionados à autenticação e ao registro
 * de usuários.
 * </p>
 */
class AuthControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MyUserDetailsService userDetailsService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testa o endpoint de autenticação de usuário com credenciais válidas.
     */
    @Test
    void deveAutenticarUsuarioComSucesso() {
        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setUsername("username");
        authRequest.setPassword("password");

        User user = new User();
        user.setLogin("username");
        user.setPassword("encodedPassword");

        when(userDetailsService.loadUserByUsername(authRequest.getUsername()))
                .thenReturn(new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(),
                        new ArrayList<>()));
        when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(authRequest.getUsername())).thenReturn("jwtToken");

        ResponseEntity<?> response = authController.createAuthenticationToken(authRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("message"));
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("token"));
        assertEquals("Authentication successful", ((Map<?, ?>) response.getBody()).get("message"));
        assertEquals("jwtToken", ((Map<?, ?>) response.getBody()).get("token"));
    }

    /**
     * Testa o endpoint de autenticação de usuário com credenciais inválidas.
     */
    @Test
    void deveRetornarErroAoAutenticarUsuarioComCredenciaisInvalidas() {
        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setUsername("username");
        authRequest.setPassword("wrongPassword");

        User user = new User();
        user.setLogin("username");
        user.setPassword("encodedPassword");

        when(userDetailsService.loadUserByUsername(authRequest.getUsername()))
                .thenReturn(new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(),
                        new ArrayList<>()));
        when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(false);

        ResponseEntity<?> response = authController.createAuthenticationToken(authRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Invalid login or password", responseBody.get("message"));
        assertEquals(401, responseBody.get("errorCode"));

        verify(jwtUtil, never()).generateToken(any());
    }

    /**
     * Testa o endpoint de registro de novo usuário com sucesso.
     */
    @Test
    void deveRegistrarUsuarioComSucesso() {
        User user = new User();
        user.setLogin("username");
        user.setEmail("email@example.com");
        user.setPassword("password");

        when(userService.save(any(User.class))).thenReturn(user);
        when(userService.existsByEmail(user.getEmail())).thenReturn(false);
        when(userService.existsByLogin(user.getLogin())).thenReturn(false);

        ResponseEntity<?> response = authController.createUser(user.toRequestDTO());

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User created successfully", responseBody.get("message"));
        assertEquals(201, responseBody.get("errorCode"));

        verify(userService, times(1)).save(any(User.class));
    }

    /**
     * Testa o endpoint de registro de novo usuário com email já cadastrado.
     */
    @Test
    void deveRetornarErroAoRegistrarUsuarioComEmailExistente() {
        User user = new User();
        user.setLogin("username");
        user.setEmail("email@example.com");

        when(userService.existsByEmail(user.getEmail())).thenReturn(true);

        ResponseEntity<?> response = authController.createUser(user.toRequestDTO());

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Email already exists", responseBody.get("message"));
        assertEquals(409, responseBody.get("errorCode"));

        verify(userService, never()).save(any(User.class));
    }

    /**
     * Testa o endpoint de registro de novo usuário com login já cadastrado.
     */
    @Test
    void deveRetornarErroAoRegistrarUsuarioComLoginExistente() {
        User user = new User();
        user.setLogin("username");
        user.setEmail("email@example.com");

        when(userService.existsByLogin(user.getLogin())).thenReturn(true);

        ResponseEntity<?> response = authController.createUser(user.toRequestDTO());

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Login already exists", responseBody.get("message"));
        assertEquals(409, responseBody.get("errorCode"));

        verify(userService, never()).save(any(User.class));
    }
}
