package com.desafio.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.desafio.entity.User;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) para encapsular as informações de um usuário na
 * resposta da API.
 * <p>
 * Este DTO é utilizado para transferir dados do back-end para o front-end.
 * Inclui informações básicas do usuário e sua lista de carros associados.
 * </p>
 */
@Getter
@Setter
public class UserResponseDTO {

    /**
     * Identificador único do usuário.
     */
    private Long id;

    /**
     * Login do usuário.
     */
    private String login;

    /**
     * E-mail do usuário.
     */
    private String email;

    /**
     * Primeiro nome do usuário.
     */
    private String firstName;

    /**
     * Sobrenome do usuário.
     */
    private String lastName;

    /**
     * Data de nascimento do usuário.
     */
    private LocalDate birthday;

    /**
     * Telefone do usuário.
     */
    private String phone;

    /**
     * Lista de carros associados ao usuário.
     */
    private List<CarResponseDTO> cars;

    /**
     * Construtor completo para UserResponseDTO.
     * 
     * @param id        Identificador único do usuário.
     * @param login     Login do usuário.
     * @param email     E-mail do usuário.
     * @param firstName Primeiro nome do usuário.
     * @param lastName  Sobrenome do usuário.
     * @param birthday  Data de nascimento do usuário.
     * @param phone     Telefone do usuário.
     * @param cars      Lista de carros associados ao usuário.
     */
    public UserResponseDTO(Long id, String login, String email, String firstName, String lastName, LocalDate birthday,
            String phone, List<CarResponseDTO> cars) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.phone = phone;
        this.cars = cars;
    }

    /**
     * Converte uma entidade {@link User} para um DTO {@link UserResponseDTO}.
     *
     * @param user A entidade {@link User}.
     * @return Uma instância de {@link UserResponseDTO}.
     * @throws IllegalArgumentException se a entidade de usuário for nula.
     */
    public static UserResponseDTO fromEntity(User user) {
        if (user == null) {
            throw new IllegalArgumentException("A entidade User não pode ser nula.");
        }

        return new UserResponseDTO(
                user.getId(),
                user.getLogin(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthday(),
                user.getPhone(),
                user.getCars() != null
                        ? user.getCars().stream()
                                .map(car -> new CarResponseDTO(
                                        car.getId(),
                                        car.getYear(),
                                        car.getLicensePlate(),
                                        car.getModel(),
                                        car.getColor(),
                                        car.getUser() != null ? car.getUser().getId() : null))
                                .collect(Collectors.toList())
                        : null
        );
    }

    /**
     * Método auxiliar para converter um objeto {@link User} para um
     * {@link UserResponseDTO}.
     * <p>
     * Este método é útil para adaptar o formato das respostas em endpoints que
     * precisam incluir informações detalhadas de usuários e seus carros
     * associados.
     * </p>
     * 
     * @param user Objeto {@link User} a ser convertido.
     * @return Um objeto {@link UserResponseDTO} preenchido com os dados do
     *         usuário.
     */
    public static UserResponseDTO convertToDto(User user) {
        if (user == null) {
            throw new IllegalArgumentException("A entidade User não pode ser nula.");
        }

        return new UserResponseDTO(
                user.getId(),
                user.getLogin(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthday(),
                user.getPhone(),
                user.getCars() != null
                        ? user.getCars().stream()
                                .map(car -> new CarResponseDTO(
                                        car.getId(),
                                        car.getYear(),
                                        car.getLicensePlate(),
                                        car.getModel(),
                                        car.getColor(),
                                        car.getUser() != null ? car.getUser().getId() : null))
                                .collect(Collectors.toList())
                        : null
        );
    }
}
