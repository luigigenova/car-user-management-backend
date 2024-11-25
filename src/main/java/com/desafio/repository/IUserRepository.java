package com.desafio.repository;

import com.desafio.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositório de acesso a dados para a entidade User.
 * Fornece métodos para operações CRUD e consultas específicas de usuário.
 */
public interface IUserRepository extends JpaRepository<User, Long> {

    /**
     * Busca um usuário pelo login.
     *
     * @param login Login do usuário.
     * @return Um Optional contendo o usuário, caso encontrado.
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

    /**
     * Busca todos os usuários com os carros já carregados.
     * 
     * @return Lista de usuários.
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cars")
    List<User> findAllWithCars();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cars WHERE u.id = :id")
    Optional<User> findByIdWithCars(@Param("id") Long id);
}
