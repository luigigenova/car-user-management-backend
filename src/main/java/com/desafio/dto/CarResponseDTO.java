package com.desafio.dto;

import com.desafio.entity.Car;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para encapsular os dados de resposta para operações relacionadas a carros.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarResponseDTO {

    /** ID do carro */
    private Long id;

    /** Modelo do carro */
    private String model;

    /** Cor do carro */
    private String color;

    /** Ano de fabricação do carro */
    private Integer year;

    /** Placa do carro */
    private String licensePlate;

    /**
     * ID do usuário associado ao carro.
     */
    private Long userId;

    /**
     * Converte uma entidade Car para o DTO de resposta.
     *
     * @param car Entidade Car.
     * @return Instância do DTO CarResponseDTO.
     */
    public static CarResponseDTO fromEntity(Car car) {
        CarResponseDTO dto = new CarResponseDTO();
        dto.setId(car.getId());
        dto.setModel(car.getModel());
        dto.setColor(car.getColor());
        dto.setYear(car.getYear());
        dto.setLicensePlate(car.getLicensePlate());
        dto.setUserId(car.getUser().getId());
        return dto;
    }

    /**
     * Construtor com campos nomeados para suporte à desserialização do Jackson.
     *
     * @param id Identificador único do carro.
     * @param year Ano do carro.
     * @param licensePlate Placa do carro.
     * @param model Modelo do carro.
     * @param color Cor do carro.
     * @param userId Identificador do usuário associado ao carro.
     */
    @JsonCreator
    public CarResponseDTO(
            @JsonProperty("id") Long id,
            @JsonProperty("year") Integer year,
            @JsonProperty("licensePlate") String licensePlate,
            @JsonProperty("model") String model,
            @JsonProperty("color") String color,
            @JsonProperty("userId") Long userId) {
        this.id = id;
        this.year = year;
        this.licensePlate = licensePlate;
        this.model = model;
        this.color = color;
        this.userId = userId;
    }
}
