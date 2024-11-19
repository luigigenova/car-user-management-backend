package com.desafio.controller;

import com.desafio.dto.AuthRequestDTO;
import com.desafio.dto.UserRequestDTO;
import com.desafio.entity.User;
import com.desafio.service.IUserService;
import com.desafio.service.impl.MyUserDetailsService;
import com.desafio.util.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador responsável pela autenticação e registro de usuários.
 * <p>
 * Este controlador oferece endpoints para autenticar usuários existentes
 * e registrar novos usuários no sistema.
 * </p>
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    private JwtUtil jwtUtil;
    private IUserService userService;
    private final MyUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(MyUserDetailsService userDetailsService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                          IUserService userService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /**
     * Autentica o usuário com as credenciais fornecidas e retorna um token JWT.
     *
     * @param authRequest DTO contendo as credenciais do usuário (username e password).
     * @return {@link ResponseEntity} com o token JWT ou mensagem de erro.
     */
    @PostMapping("/signin")
    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful, JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Invalid login or password")
    })
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequestDTO authRequest) {
        try {
            String username = authRequest.getUsername();
            String password = authRequest.getPassword();

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (passwordEncoder.matches(password, userDetails.getPassword())) {
                String jwt = jwtUtil.generateToken(userDetails.getUsername());
                return ResponseEntity.ok(Map.of("message", "Authentication successful", "token", jwt));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid login or password", "errorCode", 401));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid login or password", "errorCode", 401));
        }
    }

    /**
     * Registra um novo usuário no sistema.
     *
     * @param userDTO DTO contendo os dados do novo usuário.
     * @return {@link ResponseEntity} com a mensagem de sucesso ou erro.
     */
    @PostMapping("/signup")
    @Operation(summary = "Register user", description = "Create a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Missing fields"),
            @ApiResponse(responseCode = "409", description = "Email or login already exists"),
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "500", description = "Invalid fields")
    })
    public ResponseEntity<?> createUser(@RequestBody UserRequestDTO userDTO) {
        try {
            if (userDTO.getEmail() == null || userDTO.getLogin() == null || userDTO.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Missing fields", "errorCode", 400));
            }

            User user = new User(userDTO);

            if (userService.existsByEmail(user.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Email already exists", "errorCode", 409));
            }

            if (userService.existsByLogin(user.getLogin())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Login already exists", "errorCode", 409));
            }

            userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "User created successfully", "errorCode", 201));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Invalid fields", "errorCode", 500));
        }
    }
}
