package com.desafio.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.desafio.entity.User;
import com.desafio.service.IUserService;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gerenciar usuários.
 * Fornece endpoints para operações de CRUD (Create, Read, Update, Delete) em usuários.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User Management")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * Lista todos os usuários.
     *
     * @return uma lista de todos os usuários no sistema
     */
    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista de todos os usuários")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * Busca um usuário pelo seu ID.
     *
     * @param id o ID do usuário a ser buscado
     * @return o usuário encontrado, ou status 404 caso o usuário não exista
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário pelo ID", description = "Retorna um usuário com base no ID fornecido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Cria um novo usuário.
     *
     * @param user o objeto User contendo as informações do novo usuário
     * @return o usuário criado, ou uma mensagem de erro se o email ou login já estiverem em uso
     */
    @PostMapping
    @Operation(summary = "Criar um novo usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Email já cadastrado ou Login já em uso")
    })
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email já cadastrado");
        }
        if (userService.existsByLogin(user.getLogin())) {
            return ResponseEntity.badRequest().body("Login já em uso");
        }
        return ResponseEntity.ok(userService.save(user));
    }

    /**
     * Atualiza as informações de um usuário existente.
     *
     * @param id o ID do usuário a ser atualizado
     * @param user o objeto User contendo as novas informações do usuário
     * @return o usuário atualizado, ou status 404 caso o usuário não exista
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário existente", description = "Atualiza as informações de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        if (!userService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        user.setId(id);
        return ResponseEntity.ok(userService.save(user));
    }

    /**
     * Exclui um usuário pelo seu ID.
     *
     * @param id o ID do usuário a ser excluído
     * @return status 204 (No Content) se o usuário for excluído com sucesso, ou 404 se o usuário não existir
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário pelo ID", description = "Exclui um usuário com base no ID fornecido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
