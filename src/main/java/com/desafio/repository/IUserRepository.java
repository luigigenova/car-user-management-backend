package com.desafio.repository;

import com.desafio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório de acesso a dados para a entidade User.
 * Fornece métodos para operações CRUD e consultas específicas de usuário.
 */
public interface IUserRepository extends JpaRepository<User, Long> {

    /**
     * Busca um usuário pelo login.
     * 
     * @param login o login do usuário
     * @return o usuário com o login fornecido, ou null se não encontrado
     */
    User findByLogin(String login);

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
}
