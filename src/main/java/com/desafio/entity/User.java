package com.desafio.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.desafio.dto.UserRequestDTO;

/**
 * Entidade que representa um usuário no sistema.
 * <p>
 * Cada instância desta classe mapeia um registro na tabela {@code app_user}.
 * A entidade inclui informações pessoais do usuário e está associada a uma lista de carros.
 */
@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /** Identificador único do usuário */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** Login único do usuário */
    @Column(nullable = false, unique = true)
    private String login;

    /** Senha do usuário */
    @Column(nullable = false)
    private String password;

    /** Endereço de e-mail único do usuário */
    @Column(nullable = false, unique = true)
    private String email;

    /** Primeiro nome do usuário */
    private String firstName;

    /** Sobrenome do usuário */
    private String lastName;

    /** Data de nascimento do usuário */
    private LocalDate birthday;

    /** Número de telefone do usuário */
    private String phone;

    /**
     * Lista de carros associados ao usuário.
     * <p>
     * A associação é gerenciada com operações em cascata e remoção automática de órfãos.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Car> cars;

    /**
     * Construtor que inicializa os campos do usuário com base em um objeto {@link UserRequestDTO}.
     *
     * @param userDTO Objeto {@link UserRequestDTO} contendo os dados do usuário.
     */
    public User(UserRequestDTO userDTO) {
        this.firstName = userDTO.getFirstName();
        this.lastName = userDTO.getLastName();
        this.email = userDTO.getEmail();
        this.birthday = LocalDate.parse(userDTO.getBirthday(), DateTimeFormatter.ISO_DATE);
        this.login = userDTO.getLogin();
        this.password = userDTO.getPassword();
        this.phone = userDTO.getPhone();
    }

    /**
     * Converte a entidade {@link User} em um objeto {@link UserRequestDTO}.
     *
     * @return Instância de {@link UserRequestDTO} contendo os dados do usuário.
     */
    public UserRequestDTO toRequestDTO() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstName(this.firstName);
        dto.setLastName(this.lastName);
        dto.setEmail(this.email);
        dto.setLogin(this.login);
        dto.setPassword(this.password);
        dto.setBirthday(this.birthday.toString());
        dto.setPhone(this.phone);
        return dto;
    }
}
