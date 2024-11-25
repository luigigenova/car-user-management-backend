package com.desafio.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.desafio.dto.request.UserRequestDTO;
import com.desafio.dto.response.UserResponseDTO;
import com.desafio.entity.Car;
import com.desafio.entity.User;
import com.desafio.service.ICarService;
import com.desafio.service.IUserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para gerenciar usuários.
 * Fornece endpoints para operações de CRUD (Create, Read, Update, Delete) em
 * usuários.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User Management")
public class UserController {

    private IUserService userService;

    private ICarService carService;

    @Autowired
    public UserController(IUserService userService, ICarService carService) {
        this.carService = carService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Listar usuários com carros", description = "Retorna uma lista de todos os usuários cadastrados, incluindo os carros associados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    public ResponseEntity<List<UserResponseDTO>> getAllUsersWithCars() {
        List<UserResponseDTO> response = userService.findAllUsersWithCars();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário pelo ID", description = "Retorna um usuário com base no ID fornecido, incluindo os carros associados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            UserResponseDTO userDto = UserResponseDTO.fromEntity(user.get());
            return ResponseEntity.ok(userDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found", "errorCode", 404));
        }
    }

    @PostMapping
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Conflito: Email ou login já cadastrado"),
            @ApiResponse(responseCode = "400", description = "Campos inválidos fornecidos"),
            @ApiResponse(responseCode = "400", description = "Campos não preenchidos")
    })
    public ResponseEntity<?> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        if (userRequestDTO.getEmail() == null || userRequestDTO.getEmail().isEmpty()
                || userRequestDTO.getLogin() == null || userRequestDTO.getLogin().isEmpty()
                || userRequestDTO.getPassword() == null || userRequestDTO.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Missing fields", "errorCode", 400));
        }

        if (userService.existsByEmail(userRequestDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email already exists", "errorCode", 409));
        }

        if (userService.existsByLogin(userRequestDTO.getLogin())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Login already exists", "errorCode", 409));
        }

        try {
            User user = new User(userRequestDTO);
            if (userRequestDTO.getCars() != null && !userRequestDTO.getCars().isEmpty()) {
                List<Car> cars = userRequestDTO.getCars().stream()
                        .map(carDto -> {
                            Car car = new Car();
                            car.setYear(carDto.getYear());
                            car.setLicensePlate(carDto.getLicensePlate());
                            car.setModel(carDto.getModel());
                            car.setColor(carDto.getColor());
                            car.setUser(user);
                            return car;
                        })
                        .collect(Collectors.toList());
                user.setCars(cars);
            }

            User createdUser = userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid fields", "errorCode", 400));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza as informações de um usuário existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Erro ao processar a solicitação.")
    })
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UserRequestDTO userRequestDTO) {
        try {
            User updatedUser = userService.update(id, new User(userRequestDTO));
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Erro ao processar a solicitação.", "error", e.getMessage()));
        }
    }

    /**
     * Exclui um usuário pelo seu ID.
     *
     * @param id o ID do usuário a ser excluído.
     * @return {@link ResponseEntity} com status 204 para exclusão bem-sucedida ou
     *         404 para erro.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "Exclui um usuário com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userService.findById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found", "errorCode", 404));
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Remove a associação de um carro a um usuário.
     *
     * @param userId ID do usuário.
     * @param carId  ID do carro a ser desvinculado.
     * @return {@link ResponseEntity} indicando o sucesso ou falha da operação.
     */
    @PatchMapping("/{userId}/remove-car/{carId}")
    @Operation(summary = "Remove car from user", description = "Removes a car from the user and makes it available.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car removed successfully"),
            @ApiResponse(responseCode = "404", description = "User or car not found")
    })
    public ResponseEntity<?> removeCarFromUser(@PathVariable Long userId, @PathVariable Long carId) {
        try {
            userService.removeCarFromUser(userId, carId);
            return ResponseEntity.ok(Map.of("message", "Car removed successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    public ResponseEntity<List<UserResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        try {
            List<UserResponseDTO> users = userService.findAll(page, size, sortBy);

            if (users.isEmpty()) {
                return ResponseEntity
                        .noContent()
                        .build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Total-Count", String.valueOf(userService.getTotalCount()));
            headers.add("X-Page-Number", String.valueOf(page));
            headers.add("X-Page-Size", String.valueOf(size));

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .build();
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    /**
     * Endpoint público para listar carros disponíveis.
     * 
     * @return Lista de carros disponíveis.
     */
    @GetMapping("/available-cars")
    @Operation(summary = "Listar carros disponíveis", description = "Retorna todos os carros que não estão associados a nenhum usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de carros disponíveis retornada com sucesso."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar a solicitação.")
    })
    public ResponseEntity<?> getAvailableCars() {
        try {
            List<Car> availableCars = carService.findAvailableCars();

            if (availableCars.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(availableCars);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erro ao buscar carros disponíveis.",
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{userId}/add-cars")
    @Operation(summary = "Associate cars to user", description = "Associates selected cars to the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cars associated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid car IDs")
    })
    public ResponseEntity<?> addCarsToUser(@PathVariable Long userId, @RequestBody List<Long> carIds) {
        try {
            userService.addCarsToUser(userId, carIds);
            return ResponseEntity.ok(Map.of("message", "Cars associated successfully."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        }
    }

}
