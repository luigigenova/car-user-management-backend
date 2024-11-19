package com.desafio.controller;

import com.desafio.dto.CarRequestDTO;
import com.desafio.dto.CarResponseDTO;
import com.desafio.entity.Car;
import com.desafio.entity.User;
import com.desafio.service.ICarService;
import com.desafio.service.IUserService;
import com.desafio.service.impl.MyUserDetailsService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador que gerencia as operações relacionadas aos carros.
 * Disponibiliza endpoints REST para criação, atualização, remoção e
 * listagem de carros pertencentes a um usuário autenticado.
 */
@RestController
@RequestMapping("/api/cars")
public class CarController {

    /**
     * Serviço responsável pelas operações relacionadas aos carros.
     */
    private final ICarService carService;

    /**
     * Serviço responsável pelos detalhes do usuário autenticado.
     */
    private final MyUserDetailsService userDetailsService;

    /**
     * Serviço responsável pelas operações relacionadas aos usuários.
     */
    private final IUserService userService;

    /**
     * Construtor para injeção de dependência do serviço de carros.
     *
     * @param carService         Serviço responsável pela lógica de negócios.
     * @param userDetailsService Serviço responsável pelos detalhes do usuário autenticado.
     * @param userService        Serviço responsável pelas operações relacionadas aos usuários.
     */
    @Autowired
    public CarController(ICarService carService, MyUserDetailsService userDetailsService, IUserService userService) {
        this.carService = carService;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    /**
     * Cria um novo carro associado ao usuário autenticado.
     *
     * @param carRequestDTO DTO contendo os dados do carro a ser criado.
     * @param principal     Objeto Principal que representa o usuário autenticado.
     * @return O carro criado, encapsulado em um DTO de resposta ou mensagem de erro.
     */
    @PostMapping
    @Operation(summary = "Criar novo carro", description = "Cria um novo carro associado ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carro criado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Placa já existente"),
            @ApiResponse(responseCode = "400", description = "Campos inválidos")
    })
    public ResponseEntity<?> createCar(@RequestBody CarRequestDTO carRequestDTO, Principal principal) {
        try {
            String username = principal.getName(); // Captura o username diretamente
            Car car = carRequestDTO.toEntity();

            if (carService.existsByLicensePlate(car.getLicensePlate())) {
                return ResponseEntity.status(409).body(Map.of("message", "License plate already exists", "error", true));
            }

            if (car.getUser() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                User user = userService.findByUsername(userDetails.getUsername());
                car.setUser(user);
            }

            carService.saveCar(car);
            return ResponseEntity.ok(Map.of("message", "Car created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "An error occurred", "error", e.getMessage()));
        }
    }

    /**
     * Atualiza os dados de um carro existente.
     *
     * @param id            ID do carro a ser atualizado.
     * @param carRequestDTO DTO contendo os novos dados do carro.
     * @param principal     Objeto Principal que representa o usuário autenticado.
     * @return O carro atualizado, encapsulado em um DTO de resposta ou mensagem de erro.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar carro existente", description = "Atualiza os dados de um carro associado ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carro atualizado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Placa já existente"),
            @ApiResponse(responseCode = "400", description = "Campos inválidos")
    })
    public ResponseEntity<?> updateCar(@PathVariable Long id, @RequestBody CarRequestDTO carRequestDTO, Principal principal) {
        try {
            String username = principal.getName();
            Car updatedCar = carRequestDTO.toEntity();

            if (carService.existsByLicensePlateAndIdNot(updatedCar.getLicensePlate(), id)) {
                return ResponseEntity.status(409).body(Map.of("message", "License plate already exists", "errorCode", 409));
            }

            if (updatedCar.getUser() == null) {
                User user = userService.findByUsername(username);
                updatedCar.setUser(user);
            }

            Car savedCar = carService.updateCar(id, updatedCar);
            return ResponseEntity.ok(CarResponseDTO.fromEntity(savedCar));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", "Invalid fields", "errorCode", 400));
        }
    }

    /**
     * Lista todos os carros pertencentes ao usuário autenticado.
     *
     * @param principal Objeto Principal que representa o usuário autenticado.
     * @return Lista de carros do usuário, encapsulada em DTOs de resposta ou mensagem de erro.
     */
    @GetMapping
    @Operation(summary = "Listar carros", description = "Lista todos os carros pertencentes ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de carros retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token não enviado ou inválido")
    })
    public ResponseEntity<?> getCars(Principal principal) {
        try {
            String username = principal.getName();
            User user = userService.findByUsername(username);
            List<Car> cars = carService.getCarsByUserId(user.getId());
            List<CarResponseDTO> response = cars.stream()
                    .map(CarResponseDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized", "errorCode", 401));
        }
    }

    /**
     * Busca um carro pelo seu ID.
     *
     * @param id o ID do carro a ser buscado
     * @return {@link ResponseEntity} contendo o carro encontrado ou uma mensagem
     *         de erro.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar carro pelo ID", description = "Retorna um carro com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carro encontrado"),
            @ApiResponse(responseCode = "404", description = "Carro não encontrado")
    })
    public ResponseEntity<?> getCarById(@PathVariable Long id) {
        Optional<Car> car = carService.findById(id);
        if (car.isPresent()) {
            return ResponseEntity.ok(car.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Car not found", "errorCode", 404));
        }
    }

    /**
     * Remove um carro associado ao usuário autenticado.
     *
     * @param id        ID do carro a ser removido.
     * @param principal Objeto Principal que representa o usuário autenticado.
     * @return Resposta sem conteúdo ou mensagem de erro.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover carro", description = "Remove um carro associado ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Carro removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token não enviado ou inválido")
    })
    public ResponseEntity<?> deleteCar(@PathVariable Long id, Principal principal) {
        try {
            String username = principal.getName();
            User user = userService.findByUsername(username);
            carService.deleteCar(id, user.getId());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized", "errorCode", 401));
        }
    }
}
