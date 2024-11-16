package com.desafio.service;

import com.desafio.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para gerenciamento de usuários.
 * Define operações para criação, consulta, atualização e exclusão de usuários.
 */
public interface IUserService {

    /**
     * Lista todos os usuários.
     * 
     * @return uma lista de todos os usuários
     */
    List<User> findAll();

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
}
