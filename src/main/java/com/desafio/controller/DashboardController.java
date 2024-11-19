package com.desafio.controller;

import com.desafio.dto.UserResponseDTO;
import com.desafio.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller para gerenciar o Dashboard.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private IUserService userService;

    /**
     * Endpoint para listar todos os usuários e seus carros.
     * 
     * @return Lista de usuários com seus carros.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.findAllUsersWithCars();
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint para retornar estatísticas gerais do sistema.
     * 
     * @return Mapa contendo o total de usuários e o total de carros.
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Integer>> getStatistics() {
        Map<String, Integer> statistics = userService.getStatistics();
        return ResponseEntity.ok(statistics);
    }
}
