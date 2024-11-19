package com.desafio.service.impl;

import com.desafio.dto.UserResponseDTO;
import com.desafio.entity.User;
import com.desafio.repository.IUserRepository;
import com.desafio.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciar operações relacionadas a usuários.
 */
@Service
public class UserServiceImpl implements IUserService {

    private IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retorna uma lista de todos os usuários.
     * 
     * @return lista de usuários
     */
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Busca um usuário pelo ID.
     * 
     * @param id o ID do usuário
     * @return um Optional contendo o usuário, se encontrado
     */
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Salva um novo usuário no banco de dados, realizando validações de
     * unicidade de email e formato dos dados.
     * 
     * @param user o usuário a ser salvo
     * @return o usuário salvo
     * @throws DataIntegrityViolationException se o email já estiver cadastrado
     * @throws IllegalArgumentException        se o email ou senha forem inválidos
     */
    @Override
    public User save(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Dados inválidos");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Dados inválidos");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DataIntegrityViolationException("Email já cadastrado");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Exclui um usuário pelo ID.
     * 
     * @param id o ID do usuário a ser excluído
     */
    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Busca um usuário pelo login.
     * 
     * @param login o login do usuário
     * @return um Optional contendo o usuário, se encontrado
     */
    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    /**
     * Verifica se existe um usuário com o email fornecido.
     * 
     * @param email o email a ser verificado
     * @return true se o email já estiver cadastrado, false caso contrário
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Verifica se existe um usuário com o login fornecido.
     * 
     * @param login o login a ser verificado
     * @return true se o login já estiver cadastrado, false caso contrário
     */
    @Override
    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    /**
     * Retorna todos os usuários com seus carros.
     *
     * @return Lista de {@link UserResponseDTO}.
     */
    public List<UserResponseDTO> findAllUsersWithCars() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponseDTO::fromEntity) // Converte cada entidade para DTO
                .collect(Collectors.toList());
    }

    /**
     * Retorna estatísticas gerais do sistema.
     * 
     * @return Mapa contendo o total de usuários e o total de carros.
     */
    public Map<String, Integer> getStatistics() {
        List<User> users = userRepository.findAll();

        int totalUsers = users.size();
        int totalCars = users.stream()
                .mapToInt(user -> user.getCars().size())
                .sum();

        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("totalUsers", totalUsers);
        statistics.put("totalCars", totalCars);

        return statistics;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByLogin(username).orElse(null);
    }
}