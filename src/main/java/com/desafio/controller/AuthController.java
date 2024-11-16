package com.desafio.controller;

import com.desafio.dto.UserDTO;
import com.desafio.entity.User;
import com.desafio.security.JwtUtil;
import com.desafio.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador responsável pela autenticação e registro de usuários.
 * Fornece endpoints para login e cadastro.
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IUserService userService;

    /**
     * Endpoint para autenticação de usuários.
     * 
     * @param authRequest Mapa contendo as credenciais do usuário (username e password).
     * @return Um token JWT se a autenticação for bem-sucedida ou um erro 401 caso contrário.
     */
    @PostMapping("/signin")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody Map<String, String> authRequest) {
        try {
            String username = authRequest.get("username");
            String password = authRequest.get("password");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            User user = userService.findByLogin(username).orElseThrow();
            String jwt = jwtUtil.generateToken(user.getLogin());

            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }
    }

    /**
     * Endpoint para registro de novos usuários.
     * 
     * @param userDTO Objeto contendo os dados do usuário a ser registrado.
     * @return Mensagem de sucesso ou erro caso o email já esteja cadastrado.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        if (userService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email já cadastrado"));
        }
        userService.save(new User(userDTO));
        return ResponseEntity.ok(Map.of("message", "Usuário cadastrado com sucesso!"));
    }
}
