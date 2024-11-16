package com.desafio.dto;

import lombok.Data;
import java.util.Date;

/**
 * Data Transfer Object (DTO) para representar as informações de um usuário.
 * Esta classe encapsula os dados que serão transferidos entre a camada de apresentação
 * e a camada de serviço, facilitando a comunicação entre o front-end e o back-end.
 */
@Data
public class UserDTO {
   
    /** Primeiro nome do usuário */
    private String firstName;

    /** Sobrenome do usuário */
    private String lastName;

    /** E-mail do usuário */
    private String email;

    /** Data de nascimento do usuário */
    private Date birthday;

    /** Login do usuário */
    private String login;

    /** Nome de usuário (username) para autenticação */
    private String username;

    /** Senha do usuário */
    private String password;

    /** Telefone do usuário */
    private String phone;

    /**
     * Construtor para inicializar o DTO do usuário com todos os campos necessários.
     *
     * @param firstName O primeiro nome do usuário
     * @param lastName O sobrenome do usuário
     * @param email O e-mail do usuário
     * @param birthday A data de nascimento do usuário
     * @param login O login do usuário
     * @param password A senha do usuário
     * @param phone O telefone do usuário
     */
    public UserDTO(String firstName, String lastName, String email, Date birthday, String login, String password, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthday = birthday;
        this.login = login;
        this.password = password;
        this.phone = phone;
    }
}
