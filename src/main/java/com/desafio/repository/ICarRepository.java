package com.desafio.repository;

import com.desafio.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface que define as operações de acesso a dados para a entidade Car.
 */
@Repository
public interface ICarRepository extends JpaRepository<Car, Long> {

    /**
     * Busca todos os carros de um usuário específico.
     *
     * @param userId ID do usuário.
     * @return Lista de carros do usuário.
     */
    List<Car> findByUserId(Long userId);

    /**
     * Verifica se existe um carro com a placa especificada.
     *
     * @param licensePlate Placa do carro.
     * @return true se a placa já estiver cadastrada, caso contrário false.
     */
    boolean existsByLicensePlate(String licensePlate);

    /**
     * Verifica se existe um carro com a placa especificada, excluindo um ID.
     *
     * @param licensePlate Placa do carro.
     * @param id ID do carro a ser excluído da verificação.
     * @return true se a placa já estiver cadastrada em outro carro, caso contrário false.
     */
    boolean existsByLicensePlateAndIdNot(String licensePlate, Long id);
}

