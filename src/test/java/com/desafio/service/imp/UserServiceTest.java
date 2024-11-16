package com.desafio.service.imp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.desafio.dto.UserDTO;
import com.desafio.entity.User;
import com.desafio.repository.IUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Date;

/**
 * Classe de teste para o serviço de usuários {@link UserService}.
 * Verifica os cenários de cadastro de um novo usuário, tentativa de cadastro
 * com email já existente e tentativa de cadastro com dados inválidos.
 */
public class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserService userService; // Corrigido para UserService em vez de IUserService

    /**
     * Configuração inicial dos mocks antes de cada teste.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testa o cenário de cadastro bem-sucedido de um novo usuário.
     * 
     * <p>Valida que o usuário é salvo com sucesso e que os atributos 
     * estão corretamente preenchidos.</p>
     */
    @Test
    public void testCadastroNovoUsuarioComSucesso() {
        UserDTO userDTO = new UserDTO(
            "Luigi",                  
            "Genova",                 
            "email@teste.com",        
            new Date(),               
            "loginTeste",             
            "senha123",               
            "81999998171"             
        );
        User user = new User(userDTO);

        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.save(user);

        assertNotNull(savedUser);
        assertEquals(userDTO.getEmail(), savedUser.getEmail());
        assertEquals(userDTO.getLogin(), savedUser.getLogin());
        assertEquals(userDTO.getFirstName(), savedUser.getFirstName());
        assertEquals(userDTO.getLastName(), savedUser.getLastName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Testa o cenário onde o cadastro de um usuário falha devido a um e-mail já existente.
     * 
     * <p>Valida que uma exceção {@link DataIntegrityViolationException} é lançada
     * com a mensagem "Email já cadastrado" e que o método save não é chamado.</p>
     */
    @Test
    public void testCadastroUsuarioComEmailExistente() {
        UserDTO userDTO = new UserDTO(
            "Luigi",                   
            "Genova",                  
            "email@existente.com",     
            new Date(),                
            "loginExistente",          
            "senha123",                
            "81999998171"              
        );

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            userService.save(new User(userDTO));
        });

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Testa o cenário onde o cadastro de um usuário falha devido a dados inválidos.
     * 
     * <p>Valida que uma exceção {@link IllegalArgumentException} é lançada
     * com a mensagem "Dados inválidos" e que o método save não é chamado.</p>
     */
    @Test
    public void testCadastroUsuarioComDadosInvalidos() {
        UserDTO userDTO = new UserDTO(
            "",                        
            "Genova",                  
            "",                       
            new Date(),                
            "loginInvalido",           
            "123",                     
            "81999998171"              
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.save(new User(userDTO));
        });

        assertEquals("Dados inválidos", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
