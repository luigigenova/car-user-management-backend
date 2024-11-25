package com.desafio.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe que representa a entidade Carro no sistema.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cars", uniqueConstraints = @UniqueConstraint(columnNames = "license_plate"))
public class Car {

    /**
     * Identificador único do carro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Modelo do carro.
     */
    @NotNull(message = "O modelo é obrigatório")
    private String model;

    /**
     * Ano de fabricação do carro.
     */
    @NotNull(message = "O ano é obrigatório")
    @Column(name = "`year`")
    private Integer year;

    /**
     * Cor do carro.
     */
    @NotNull(message = "A cor é obrigatória")
    private String color;

    /**
     * Placa do carro (deve ser única).
     */
    @Column(name = "license_plate", nullable = false, unique = true)
    @NotNull(message = "A placa é obrigatória")
    private String licensePlate;

    /**
     * Usuário proprietário do carro.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = true) // Permite null
    @JsonIgnore
    private User user;
}
