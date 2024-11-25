package com.desafio.service.impl;

import com.desafio.dto.response.CarResponseDTO;
import com.desafio.dto.response.UserResponseDTO;
import com.desafio.entity.Car;
import com.desafio.entity.User;
import com.desafio.repository.ICarRepository;
import com.desafio.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Classe de teste para {@link UserServiceImpl}.
 * <p>
 * Garante a cobertura de todas as funcionalidades implementadas no serviço de
 * usuários.
 * Segue boas práticas e utiliza mocks para isolar as dependências.
 * </p>
 */
class UserServiceImplTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private ICarRepository carRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveLancarExcecaoAoSalvarUsuarioComEmailDuplicado() {
        User user = criarUsuarioCompleto();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class,
                () -> userService.save(user));

        assertEquals("Email já cadastrado.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deveExcluirUsuarioComSucesso() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveRetornarUsuarioPorId() {
        User user = criarUsuarioCompleto();
        when(userRepository.findByIdWithCars(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getLogin());
        verify(userRepository, times(1)).findByIdWithCars(1L);
    }

    @Test
    void deveRetornarListaDeUsuariosComCarros() {
        User user = criarUsuarioCompleto();
        user.setCars(List.of(criarCarroCompleto()));

        when(userRepository.findAllWithCars()).thenReturn(List.of(user));

        List<UserResponseDTO> result = userService.findAllUsersWithCars();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getLogin());
        verify(userRepository, times(1)).findAllWithCars();
    }

    @Test
    void deveLancarExcecaoAoAdicionarCarrosInvalidos() {
        User user = criarUsuarioCompleto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.addCarsToUser(1L, List.of(1L, 2L)));

        assertEquals("Some cars were not found or are invalid.", exception.getMessage());
        verify(carRepository, never()).saveAll(anyList());
    }

    @Test
    void deveRetornarEstatisticasDoSistema() {
        User user = criarUsuarioCompleto();
        Car car = criarCarroCompleto();
        user.setCars(List.of(car));

        when(userRepository.count()).thenReturn(1L);
        when(userRepository.findAllWithCars()).thenReturn(List.of(user));

        Map<String, Integer> stats = userService.getStatistics();

        assertNotNull(stats);
        assertEquals(1, stats.get("totalUsers"));
        assertEquals(1, stats.get("totalCars"));
    }

    private User criarUsuarioCompleto() {
        User user = new User();
        user.setId(1L);
        user.setLogin("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhone("123456789");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    private Car criarCarroCompleto() {
        Car car = new Car();
        car.setId(1L);
        car.setLicensePlate("ABC-1234");
        car.setModel("Model X");
        car.setColor("Red");
        car.setYear(2020);
        return car;
    }
}
