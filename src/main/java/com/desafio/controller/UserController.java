package com.desafio.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.desafio.dto.UserRequestDTO;
import com.desafio.dto.UserResponseDTO;
import com.desafio.entity.Car;
import com.desafio.entity.User;
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

    @Autowired
    private IUserService userService;

    /**
     * Retorna uma lista de todos os usuários cadastrados.
     *
     * @return {@link ResponseEntity} contendo a lista de {@link UserResponseDTO}.
     */
    @GetMapping
    @Operation(summary = "Listar usuários", description = "Retorna uma lista de todos os usuários cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.findAll();

        List<UserResponseDTO> response = users.stream()
                .map(UserResponseDTO::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Busca um usuário pelo seu ID.
     *
     * @param id o ID do usuário a ser buscado
     * @return {@link ResponseEntity} contendo o usuário encontrado ou uma mensagem
     *         de erro.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário pelo ID", description = "Retorna um usuário com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found", "errorCode", 404));
        }
    }

    /**
     * Cria um novo usuário.
     *
     * @param userRequestDTO Objeto {@link UserRequestDTO} contendo as informações
     *                       do novo usuário.
     * @return {@link ResponseEntity} contendo o usuário criado ou uma mensagem de
     *         erro.
     */
    @PostMapping
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Conflito: Email ou login já cadastrado"),
            @ApiResponse(responseCode = "400", description = "Campos inválidos fornecidos")
    })
    public ResponseEntity<?> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        if (userService.existsByEmail(userRequestDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email already exists", "errorCode", 409));
        }
        if (userService.existsByLogin(userRequestDTO.getLogin())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Login already exists", "errorCode", 409));
        }

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
    }

    /**
     * Atualiza as informações de um usuário existente.
     *
     * @param id   o ID do usuário a ser atualizado.
     * @param user Objeto {@link User} contendo as novas informações do usuário.
     * @return {@link ResponseEntity} contendo o usuário atualizado ou uma mensagem
     *         de erro.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza as informações de um usuário existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        if (!userService.findById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found", "errorCode", 404));
        }
        user.setId(id);
        User updatedUser = userService.save(user);
        return ResponseEntity.ok(updatedUser);
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
}
