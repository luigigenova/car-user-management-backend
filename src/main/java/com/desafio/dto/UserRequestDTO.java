package com.desafio.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) para encapsular as informações de um usuário.
 * <p>Este DTO é utilizado para transferir dados entre as camadas da aplicação,
 * especialmente entre o front-end e o back-end. Ele inclui validações para
 * garantir a integridade dos dados recebidos.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    /**
     * Identificador único do usuário.
     */
    private Long id;

    /**
     * Primeiro nome do usuário.
     */
    @NotBlank(message = "O primeiro nome é obrigatório")
    private String firstName;

    /**
     * Sobrenome do usuário.
     */
    @NotBlank(message = "O sobrenome é obrigatório")
    private String lastName;

    /**
     * Endereço de e-mail do usuário.
     */
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    /**
     * Data de nascimento do usuário.
     */
    @NotNull(message = "A data de nascimento é obrigatória")
    private String birthday;

    /**
     * Login do usuário para autenticação.
     */
    @NotBlank(message = "O login é obrigatório")
    private String login;

    /**
     * Nome de usuário (username) para autenticação.
     */
    @NotBlank(message = "O username é obrigatório")
    private String username;

    /**
     * Senha do usuário.
     */
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
    private String password;

    /**
     * Telefone do usuário.
     */
    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "O telefone deve conter 10 ou 11 dígitos")
    private String phone;

    /**
     * Lista de carros associados ao usuário.
     */
    private List<CarResponseDTO> cars;

    /**
     * Construtor para inicializar o DTO do usuário com todos os campos necessários.
     * 
     * @param id Identificador único do usuário
     * @param firstName Primeiro nome do usuário
     * @param lastName Sobrenome do usuário
     * @param email Endereço de e-mail do usuário
     * @param birthday Data de nascimento do usuário
     * @param login Login do usuário
     * @param password Senha do usuário
     * @param phone Telefone do usuário
     */
    public UserRequestDTO(Long id, String firstName, String lastName, String email, String birthday, String login, String password, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthday = birthday;
        this.login = login;
        this.password = password;
        this.phone = phone;
    }

    /**
     * Construtor para inicializar o DTO do usuário sem o campo ID.
     * 
     * @param firstName Primeiro nome do usuário
     * @param lastName Sobrenome do usuário
     * @param email Endereço de e-mail do usuário
     * @param birthday Data de nascimento do usuário
     * @param login Login do usuário
     * @param password Senha do usuário
     * @param phone Telefone do usuário
     */
    public UserRequestDTO(String firstName, String lastName, String email, String birthday, String login, String password, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthday = birthday;
        this.login = login;
        this.password = password;
        this.phone = phone;
    }
}
