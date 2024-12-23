package com.desafio.service;

import com.desafio.dto.response.CarResponseDTO;
import com.desafio.dto.response.UserResponseDTO;
import com.desafio.entity.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Serviço para gerenciamento de usuários.
 * Define operações para criação, consulta, atualização e exclusão de usuários.
 */
public interface IUserService {

    /**
     * Busca um usuário pelo ID.
     * 
     * @param id o ID do usuário
     * @return um Optional contendo o usuário, se encontrado
     */
    Optional<User> findById(Long id);

    /**
     * Salva um novo usuário ou atualiza um usuário existente.
     * 
     * @param user o usuário a ser salvo ou atualizado
     * @return o usuário salvo
     */
    User save(User user);

    /**
     * Exclui um usuário pelo ID.
     * 
     * @param id o ID do usuário a ser excluído
     */
    void deleteById(Long id);

    /**
     * Busca um usuário pelo login.
     * 
     * @param login o login do usuário
     * @return um Optional contendo o usuário, se encontrado
     */
    Optional<User> findByLogin(String login);

    /**
     * Verifica se existe um usuário com o email fornecido.
     * 
     * @param email o email a ser verificado
     * @return true se o email já estiver cadastrado, false caso contrário
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se existe um usuário com o login fornecido.
     * 
     * @param login o login a ser verificado
     * @return true se o login já estiver cadastrado, false caso contrário
     */
    boolean existsByLogin(String login);

    List<UserResponseDTO> findAllUsersWithCars();

    Map<String, Integer> getStatistics();

    User findByUsername(String username);

    void removeCarFromUser(Long userId, Long carId);

    List<UserResponseDTO> findAll(int page, int size, String sortBy);

    long getTotalCount();

    List<CarResponseDTO> getAvailableCars();

    User update(Long id, User user);

    void addCarsToUser(Long userId, List<Long> carIds);
}
