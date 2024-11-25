package com.desafio.dto.request;

import com.desafio.entity.Car;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para encapsular os dados de requisição para operações relacionadas a carros.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarRequestDTO {

    /** Modelo do carro */
    @NotBlank(message = "O modelo do carro é obrigatório")
    private String model;

    /** Marca do carro */
    @NotBlank(message = "A cor do carro é obrigatória")
    private String color;

    /** Ano de fabricação do carro */
    @NotNull(message = "O ano de fabricação é obrigatório")
    @Positive(message = "O ano de fabricação deve ser um número positivo")
    private Integer year;

    /** Placa do carro */
    @NotBlank(message = "A placa do carro é obrigatória")
    @Pattern(regexp = "^[A-Z]{3}-\\d{4}$", message = "A placa deve seguir o formato ABC-1234")
    private String licensePlate;

    /**
     * Converte o DTO para a entidade Car.
     *
     * @return Instância da entidade Car.
     */
    public Car toEntity() {
        Car car = new Car();
        car.setModel(this.model);
        car.setColor(this.color);
        car.setYear(this.year);
        car.setLicensePlate(this.licensePlate);
        return car;
    }
}
