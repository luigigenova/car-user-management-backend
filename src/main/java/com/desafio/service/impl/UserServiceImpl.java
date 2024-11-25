package com.desafio.service.impl;

import com.desafio.dto.response.CarResponseDTO;
import com.desafio.dto.response.UserResponseDTO;
import com.desafio.entity.Car;
import com.desafio.entity.User;
import com.desafio.repository.ICarRepository;
import com.desafio.repository.IUserRepository;
import com.desafio.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelas operações relacionadas a usuários.
 * Implementa regras de negócio, validações e manipulação de dados.
 */
@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ICarRepository carRepository;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository, PasswordEncoder passwordEncoder,
            ICarRepository carRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.carRepository = carRepository;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findByIdWithCars(id);
    }

    /**
     * Salva um novo usuário no banco de dados, aplicando validações de unicidade e
     * segurança.
     *
     * @param user Objeto {@link User} a ser salvo.
     * @return Usuário salvo no banco de dados.
     * @throws DataIntegrityViolationException se o email já estiver cadastrado.
     * @throws IllegalArgumentException        se dados inválidos forem fornecidos.
     */
    @Override
    public User save(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Dados inválidos: Email não pode estar vazio.");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Dados inválidos: Senha deve ter no mínimo 6 caracteres.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DataIntegrityViolationException("Email já cadastrado.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Exclui um usuário pelo ID.
     *
     * @param id ID do usuário a ser excluído.
     */
    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Busca um usuário pelo login.
     *
     * @param login Login do usuário.
     * @return {@link Optional} contendo o usuário, se encontrado.
     */
    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    /**
     * Verifica se existe um usuário com o email fornecido.
     *
     * @param email Email a ser verificado.
     * @return True se o email já estiver cadastrado, False caso contrário.
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Verifica se existe um usuário com o login fornecido.
     *
     * @param login Login a ser verificado.
     * @return True se o login já estiver cadastrado, False caso contrário.
     */
    @Override
    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    /**
     * Retorna todos os usuários com seus carros associados.
     *
     * @return Lista de {@link UserResponseDTO}.
     */
    @Override
    public List<UserResponseDTO> findAllUsersWithCars() {
        return userRepository.findAllWithCars().stream()
                .map(UserResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retorna estatísticas gerais do sistema.
     *
     * @return Mapa contendo total de usuários e total de carros.
     */
    @Override
    public Map<String, Integer> getStatistics() {
        int totalUsers = (int) userRepository.count();
        int totalCars = userRepository.findAllWithCars().stream()
                .mapToInt(user -> user.getCars().size())
                .sum();

        return Map.of("totalUsers", totalUsers, "totalCars", totalCars);
    }

    /**
     * Busca um usuário pelo nome de usuário.
     *
     * @param username Nome de usuário.
     * @return Objeto {@link User}, se encontrado.
     */
    @Override
    public User findByUsername(String username) {
        return userRepository.findByLogin(username).orElse(null);
    }

    /**
     * Remove a associação de um carro a um usuário.
     *
     * @param userId ID do usuário.
     * @param carId  ID do carro.
     * @throws IllegalArgumentException se o usuário ou carro não forem encontrados.
     */
    @Override
    public void removeCarFromUser(Long userId, Long carId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        Car carToRemove = user.getCars().stream()
                .filter(car -> car.getId().equals(carId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Carro não associado ao usuário."));

        // Remover a associação entre o usuário e o carro
        try {
            carToRemove.setUser(null); // Define user como null para dissociar
            carRepository.save(carToRemove); // Persiste a alteração no carro

            user.getCars().remove(carToRemove); // Remove da lista do usuário
            userRepository.save(user); // Atualiza o usuário

        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover o carro do usuário.", e);
        }
    }

    /**
     * Retorna uma página de usuários ordenada conforme os parâmetros fornecidos.
     *
     * @param page   Número da página.
     * @param size   Tamanho da página.
     * @param sortBy Campo de ordenação.
     * @return Lista de {@link UserResponseDTO}.
     */
    @Override
    public List<UserResponseDTO> findAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        try {
            return userRepository.findAll(pageable).getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (PropertyReferenceException e) {
            throw new IllegalArgumentException("Campo de ordenação inválido: " + sortBy);
        }
    }

    /**
     * Retorna o total de usuários cadastrados.
     *
     * @return Total de usuários.
     */
    @Override
    public long getTotalCount() {
        return userRepository.count();
    }

    /**
     * Converte um {@link User} para {@link UserResponseDTO}.
     *
     * @param user Usuário a ser convertido.
     * @return Objeto DTO.
     */
    private UserResponseDTO convertToDTO(User user) {
        return UserResponseDTO.fromEntity(user);
    }

    @Override
    public List<CarResponseDTO> getAvailableCars() {
        return carRepository.findAvailableCars()
                .stream()
                .map(CarResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public User update(Long userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (!existingUser.getEmail().equals(updatedUser.getEmail())
                && userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new DataIntegrityViolationException("Email já cadastrado.");
        }

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setBirthday(updatedUser.getBirthday());
        existingUser.setLogin(updatedUser.getLogin());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void addCarsToUser(Long userId, List<Long> carIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        List<Car> carsToAssociate = carRepository.findAllById(carIds);

        if (carsToAssociate.isEmpty() || carsToAssociate.size() != carIds.size()) {
            throw new IllegalArgumentException("Some cars were not found or are invalid.");
        }

        carsToAssociate.forEach(car -> car.setUser(user));
        carRepository.saveAll(carsToAssociate);
    }

}
