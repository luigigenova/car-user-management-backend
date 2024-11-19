package com.desafio.service;

import com.desafio.entity.Car;

import java.util.List;
import java.util.Optional;

/**
 * Interface que define os serviços relacionados à entidade Car.
 * Segue o princípio de Interface Segregation do SOLID, definindo métodos
 * específicos para a manipulação de carros.
 */
public interface ICarService {

    /**
     * Salva um novo carro no sistema.
     *
     * @param car Objeto Car a ser salvo.
     * @return O carro salvo.
     */
    Car saveCar(Car car);

    /**
     * Atualiza um carro existente no sistema.
     *
     * @param id  ID do carro a ser atualizado.
     * @param car Objeto Car contendo os novos dados.
     * @return O carro atualizado.
     */
    Car updateCar(Long id, Car car);

    /**
     * Obtém todos os carros associados a um usuário específico.
     *
     * @param userId ID do usuário.
     * @return Lista de carros do usuário.
     */
    List<Car> getCarsByUserId(Long userId);

    /**
     * Remove um carro associado a um usuário específico.
     *
     * @param id     ID do carro a ser removido.
     * @param userId ID do usuário proprietário.
     */
    void deleteCar(Long id, Long userId);

    boolean existsByLicensePlate(String licensePlate);

    boolean existsByLicensePlateAndIdNot(String licensePlate, Long id);

    Optional<Car> findById(Long id);
}
