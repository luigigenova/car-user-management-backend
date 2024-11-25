package com.desafio.controller;

import com.desafio.dto.request.AuthRequestDTO;
import com.desafio.dto.request.UserRequestDTO;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Classe de teste unitário para o controlador AuthController.
 * Valida os endpoints relacionados à autenticação e registro de usuários,
 * garantindo a aplicação de conceitos de S.O.L.I.D. e Design Patterns.
 */
class AuthControllerTest {

    /**
     * Mock para o utilitário de geração de tokens JWT.
     */
    @Mock
    private JwtUtil jwtUtil;

    /**
     * Mock para o serviço de carregamento de detalhes do usuário.
     */
    @Mock
    private MyUserDetailsService userDetailsService;

    /**
     * Mock para o serviço de operações de usuário.
     */
    @Mock
    private UserServiceImpl userService;

    /**
     * Mock para o codificador de senhas.
     */
    @Mock
    private PasswordEncoder passwordEncoder;

    /**
     * Controlador sendo testado, com dependências injetadas via Mockito.
     */
    @InjectMocks
    private AuthController authController;

    /**
     * Configuração inicial para o Mockito antes de cada teste.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testa o endpoint de autenticação de usuário com credenciais válidas.
     */
    @Test
    void deveAutenticarUsuarioComSucesso() {
        AuthRequestDTO authRequest = createAuthRequest("username", "password");

        UserDetails userDetails = createUserDetails("username", "encodedPassword");
        User user = createUser("username", "encodedPassword", "John", "Doe");

        when(userDetailsService.loadUserByUsername(authRequest.getUsername())).thenReturn(userDetails);
        when(passwordEncoder.matches(authRequest.getPassword(), userDetails.getPassword())).thenReturn(true);
        when(userService.findByUsername(authRequest.getUsername())).thenReturn(user);
        when(jwtUtil.generateToken(userDetails.getUsername())).thenReturn("jwtToken");

        ResponseEntity<Map<String, String>> response = authController.createAuthenticationToken(authRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Authentication successful", body.get("message"));
        assertEquals("jwtToken", body.get("token"));
        assertEquals("John Doe", body.get("name"));
    }

    /**
     * Testa o endpoint de autenticação de usuário com credenciais inválidas.
     */
    @Test
    void deveRetornarErroAoAutenticarUsuarioComCredenciaisInvalidas() {
        AuthRequestDTO authRequest = createAuthRequest("username", "wrongPassword");

        UserDetails userDetails = createUserDetails("username", "encodedPassword");

        when(userDetailsService.loadUserByUsername(authRequest.getUsername())).thenReturn(userDetails);
        when(passwordEncoder.matches(authRequest.getPassword(), userDetails.getPassword())).thenReturn(false);

        ResponseEntity<Map<String, String>> response = authController.createAuthenticationToken(authRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Invalid login or password", body.get("message"));
    }

    /**
     * Testa o endpoint de registro de novo usuário com sucesso.
     */
    @Test
    void deveRegistrarUsuarioComSucesso() {
        // Criando o UserRequestDTO com um valor válido para "birthday"
        UserRequestDTO userDTO = createUserRequestDTO("username", "email@example.com", "password");
        userDTO.setBirthday("1990-01-01"); // Adiciona uma data válida para evitar NullPointerException
    
        // Configurando os mocks para simular o comportamento esperado
        when(userService.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userService.existsByLogin(userDTO.getLogin())).thenReturn(false);
        when(userService.save(any(User.class))).thenReturn(new User(userDTO)); // Simulando a criação de um usuário
    
        // Executando o método de teste
        ResponseEntity<Map<String, String>> response = authController.createUser(userDTO);
    
        // Verificações
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("User created successfully", body.get("message"));
    
        // Garantindo que os métodos necessários foram chamados
        verify(userService, times(1)).existsByEmail(userDTO.getEmail());
        verify(userService, times(1)).existsByLogin(userDTO.getLogin());
        verify(userService, times(1)).save(any(User.class));
    }    
    
    /**
     * Testa o endpoint de registro de novo usuário com email já existente.
     */
    @Test
    void deveRetornarErroAoRegistrarUsuarioComEmailExistente() {
        UserRequestDTO userDTO = createUserRequestDTO("username", "email@example.com", "password");

        when(userService.existsByEmail(userDTO.getEmail())).thenReturn(true);

        ResponseEntity<Map<String, String>> response = authController.createUser(userDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Email already exists", body.get("message"));
        verify(userService, never()).save(any(User.class));
    }

    /**
     * Testa o endpoint de registro de novo usuário com login já existente.
     */
    @Test
    void deveRetornarErroAoRegistrarUsuarioComLoginExistente() {
        UserRequestDTO userDTO = createUserRequestDTO("username", "email@example.com", "password");

        when(userService.existsByLogin(userDTO.getLogin())).thenReturn(true);

        ResponseEntity<Map<String, String>> response = authController.createUser(userDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Login already exists", body.get("message"));
        verify(userService, never()).save(any(User.class));
    }

    /**
     * Cria uma instância de AuthRequestDTO para facilitar os testes.
     *
     * @param username Nome do usuário.
     * @param password Senha do usuário.
     * @return Instância de AuthRequestDTO.
     */
    private AuthRequestDTO createAuthRequest(String username, String password) {
        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setUsername(username);
        authRequest.setPassword(password);
        return authRequest;
    }

    /**
     * Cria uma instância de UserDetails para facilitar os testes.
     *
     * @param username Nome do usuário.
     * @param password Senha codificada do usuário.
     * @return Instância de UserDetails.
     */
    private UserDetails createUserDetails(String username, String password) {
        return new org.springframework.security.core.userdetails.User(username, password, Collections.emptyList());
    }

    /**
     * Cria uma instância de User para facilitar os testes.
     *
     * @param login     Login do usuário.
     * @param password  Senha do usuário.
     * @param firstName Primeiro nome do usuário.
     * @param lastName  Último nome do usuário.
     * @return Instância de User.
     */
    private User createUser(String login, String password, String firstName, String lastName) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }

    /**
     * Cria uma instância de UserRequestDTO para facilitar os testes.
     *
     * @param login    Login do usuário.
     * @param email    Email do usuário.
     * @param password Senha do usuário.
     * @return Instância de UserRequestDTO.
     */
    private UserRequestDTO createUserRequestDTO(String login, String email, String password) {
        UserRequestDTO userDTO = new UserRequestDTO();
        userDTO.setLogin(login);
        userDTO.setEmail(email);
        userDTO.setPassword(password);
        return userDTO;
    }
}
